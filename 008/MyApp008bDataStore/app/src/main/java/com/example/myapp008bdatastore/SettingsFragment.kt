package com.example.myapp008bdatastore

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.myapp008bdatastore.databinding.FragmentSettingsBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.IOException
import kotlin.random.Random

// 1. Vytvoření instance DataStore na úrovni souboru (tzv. top-level)
val Context.dataStore by preferencesDataStore(name = "gamer_profile")

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    // Stav reakčního testu: čeká na start, čeká na zelenou, nebo svítí zelená.
    private enum class ReactionState { IDLE, WAITING, GO }

    private var reactionState = ReactionState.IDLE
    private var plannedGoTime = 0L
    private var goShownTime = 0L
    private var bestReactionMs = NO_RESULT

    // Zelenou plánujeme přes vlastní Handler, ne přes View.postDelayed. Když se view při otočení
    // displeje odpojí dřív, než stihneme naplánovanou akci zrušit, View.removeCallbacks ji
    // už nenajde, zelená by doběhla nad zničeným view a aplikace by spadla.
    private val reactionHandler = Handler(Looper.getMainLooper())

    private val colorIdle = Color.parseColor("#455A64")
    private val colorWaiting = Color.parseColor("#C62828")
    private val colorGo = Color.parseColor("#2E7D32")

    // Pole jemně „dýchá“ (zmenšuje se a zvětšuje), aby bylo vidět, jestli se UI plynule překresluje.
    private var pulseAnimator: ObjectAnimator? = null

    // 2. Definice klíčů (DataStore je na rozdíl od SharedPreferences typově bezpečný)
    private val NICKNAME_KEY = stringPreferencesKey("NICKNAME")
    private val HARDCORE_KEY = booleanPreferencesKey("HARDCORE")

    // Poslední Snackbar se Zpět. Nové ukládání ho zavře, aby se undo nekřížilo s dalším zápisem.
    private var undoSnackbar: Snackbar? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dataStore = requireContext().dataStore

        // V části A vrátil getString hodnotu hned. Tady je čtení suspend funkce, takže běží
        // v korutině a výsledek dorazí až po obnovení stavu prvků. Po otočení displeje by
        // přepsalo rozepsané změny, proto načítáme jen při skutečném otevření obrazovky.
        if (savedInstanceState == null) {
            viewLifecycleOwner.lifecycleScope.launch {
                val preferences = dataStore.data.first()
                binding.etNickname.setText(preferences[NICKNAME_KEY] ?: "")
                binding.switchHardcoreMode.isChecked = preferences[HARDCORE_KEY] ?: false
            }
        }

        // Stejný reakční test jako v části A, ale tady ukládání nic nerozbije. Zelená i klepnutí
        // běží taky na hlavním vlákně, jenže delay(3000) to vlákno neblokuje: korutina se jen
        // pozastaví a vlákno dál zpracovává naplánovanou zelenou i dotyky. Zelená proto naskočí
        // včas a zpoždění zůstane v řádu jednotek ms, i když se točí kolečko. Ze stejného
        // důvodu pole během ukládání dál plynule pulzuje.
        setupReactionTest(savedInstanceState)

        binding.btnSave.setOnClickListener {
            // UI teď během čekání reaguje, takže formulář jde dál měnit. Hodnoty si proto
            // přečteme hned: uloží se to, co bylo na obrazovce při kliknutí.
            val nickname = binding.etNickname.text.toString()
            val hardcoreMode = binding.switchHardcoreMode.isChecked

            // Vypnuté tlačítko zabrání tomu, aby se ukládání spustilo víckrát najednou.
            undoSnackbar?.dismiss()
            binding.progressBar.visibility = View.VISIBLE
            binding.btnSave.isEnabled = false

            // Scope patří k view této obrazovky. Když uživatel během 3 sekund odejde (Zpět,
            // otočení displeje), korutina se zruší a k zápisu nedojde. Pro tuto ukázku to stačí,
            // aby uložení doběhlo vždy, muselo by běžet ve scope, který žije déle než obrazovka.
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    // Rozdíl oproti části A: Thread.sleep drželo hlavní vlákno 3 sekundy obsazené.
                    // delay jen pozastaví tuto korutinu a vlákno mezitím uvolní, takže dál
                    // zpracovává dotyky a překreslování. Až čas vyprší, korutina pokračuje.
                    delay(3000)

                    // Rozdíl oproti commit(): edit je suspend funkce a samotný zápis na disk
                    // provede DataStore mimo hlavní vlákno. Neúspěch hlásí výjimkou, ne booleanem.
                    // Předchozí hodnoty bereme z DataStore (ne z formuláře), aby undo vrátilo
                    // opravdu uložený stav. Null znamená, že klíč ještě neexistoval.
                    var previousNickname: String? = null
                    var previousHardcoreMode: Boolean? = null
                    dataStore.edit { preferences ->
                        previousNickname = preferences[NICKNAME_KEY]
                        previousHardcoreMode = preferences[HARDCORE_KEY]
                        preferences[NICKNAME_KEY] = nickname
                        preferences[HARDCORE_KEY] = hardcoreMode
                    }

                    showSavedSnackbar(previousNickname, previousHardcoreMode)
                } catch (e: IOException) {
                    showErrorSnackbar()
                } finally {
                    // Po zrušení korutiny už view může být pryč a binding je null,
                    // proto tu není binding!!, ale _binding?.
                    _binding?.progressBar?.visibility = View.INVISIBLE
                    _binding?.btnSave?.isEnabled = true
                }
            }
        }
    }

    // Toast z části A jen něco oznámil. Snackbar má navíc tlačítko, kterým může uživatel reagovat.
    private fun showSavedSnackbar(previousNickname: String?, previousHardcoreMode: Boolean?) {
        val snackbar = Snackbar.make(binding.root, "Profil uložen", Snackbar.LENGTH_LONG)
        snackbar.setAction("Zpět") {
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    requireContext().dataStore.edit { preferences ->
                        // Po prvním uložení nebylo co vracet, takže klíč smažeme.
                        // Zápis "" nebo false by vypadal, jako by to uživatel sám uložil.
                        if (previousNickname == null) {
                            preferences.remove(NICKNAME_KEY)
                        } else {
                            preferences[NICKNAME_KEY] = previousNickname
                        }
                        if (previousHardcoreMode == null) {
                            preferences.remove(HARDCORE_KEY)
                        } else {
                            preferences[HARDCORE_KEY] = previousHardcoreMode
                        }
                    }
                    binding.etNickname.setText(previousNickname ?: "")
                    binding.switchHardcoreMode.isChecked = previousHardcoreMode ?: false
                } catch (e: IOException) {
                    showErrorSnackbar()
                }
            }
        }
        undoSnackbar = snackbar
        snackbar.show()
    }

    private fun showErrorSnackbar() {
        Snackbar.make(binding.root, "Profil se nepodařilo uložit", Snackbar.LENGTH_LONG).show()
    }

    // Reakční test: po klepnutí čeká náhodnou dobu, pak pole zezelená a měří se,
    // jak rychle hráč klepne. Zároveň měříme, o kolik zelená naskočila později, než měla.
    private fun setupReactionTest(savedInstanceState: Bundle?) {
        bestReactionMs = savedInstanceState?.getLong(KEY_BEST_REACTION, NO_RESULT) ?: NO_RESULT
        showReactionPad("Klepni pro start", colorIdle)
        showReactionResult(lastReactionMs = NO_RESULT, lateByMs = 0)

        // ObjectAnimator počítá každý snímek animace na hlavním vlákně (přes Choreographer),
        // na rozdíl od kolečka, které kreslí RenderThread.
        pulseAnimator = ObjectAnimator.ofPropertyValuesHolder(
            binding.tvReactionPad,
            PropertyValuesHolder.ofFloat(View.SCALE_X, 1f, 0.94f),
            PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f, 0.94f)
        ).apply {
            duration = 600
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            start()
        }

        binding.tvReactionPad.setOnClickListener {
            when (reactionState) {
                ReactionState.IDLE -> startReactionRound()
                ReactionState.WAITING -> {
                    // Klepnutí před zelenou se nepočítá.
                    reactionHandler.removeCallbacks(showGoRunnable)
                    reactionState = ReactionState.IDLE
                    showReactionPad("Moc brzo! Klepni a zkus to znovu", colorIdle)
                }
                ReactionState.GO -> finishReactionRound()
            }
        }
    }

    private fun startReactionRound() {
        val waitMs = Random.nextLong(1500, 4000)
        // postDelayed měří čas stejnými hodinami jako SystemClock.uptimeMillis().
        plannedGoTime = SystemClock.uptimeMillis() + waitMs
        reactionState = ReactionState.WAITING
        showReactionPad("Čekej na zelenou…", colorWaiting)
        reactionHandler.postDelayed(showGoRunnable, waitMs)
    }

    // Runnable naplánovaný přes Handler hlavního vlákna běží na hlavním vlákně, stejně jako klepnutí.
    private val showGoRunnable = Runnable {
        goShownTime = SystemClock.uptimeMillis()
        reactionState = ReactionState.GO
        showReactionPad("KLEPNI!", colorGo)
        setPulsing(false)
    }

    private fun finishReactionRound() {
        val reactionMs = SystemClock.uptimeMillis() - goShownTime
        val lateByMs = goShownTime - plannedGoTime
        if (bestReactionMs == NO_RESULT || reactionMs < bestReactionMs) {
            bestReactionMs = reactionMs
        }
        reactionState = ReactionState.IDLE
        showReactionPad("$reactionMs ms\nKlepni pro další pokus", colorIdle)
        setPulsing(true)
        showReactionResult(reactionMs, lateByMs)
    }

    private fun showReactionPad(text: String, color: Int) {
        binding.tvReactionPad.text = text
        binding.tvReactionPad.setBackgroundColor(color)
    }

    // Zelené pole stojí, aby se do něj dobře trefovalo. start() rozjede animaci znovu od začátku.
    private fun setPulsing(pulsing: Boolean) {
        val animator = pulseAnimator ?: return
        if (pulsing) {
            animator.start()
        } else {
            animator.cancel()
            binding.tvReactionPad.scaleX = 1f
            binding.tvReactionPad.scaleY = 1f
        }
    }

    private fun showReactionResult(lastReactionMs: Long, lateByMs: Long) {
        val last = if (lastReactionMs == NO_RESULT) "–" else "$lastReactionMs ms"
        val best = if (bestReactionMs == NO_RESULT) "–" else "$bestReactionMs ms"
        var text = "Poslední: $last · Nejlepší: $best"
        // Pár desítek ms zpoždění je běžných, o zaseknutí jde až u výrazně vyšších hodnot.
        if (lateByMs > LATE_THRESHOLD_MS) {
            text += "\nZelená naskočila o $lateByMs ms později, než měla: hlavní vlákno bylo zablokované."
        }
        binding.tvReactionResult.text = text
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Nejlepší čas přežije otočení displeje. Trvale se neukládá, s profilem nesouvisí.
        outState.putLong(KEY_BEST_REACTION, bestReactionMs)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Naplánovaná zelená by jinak sáhla na binding zničeného view.
        reactionHandler.removeCallbacks(showGoRunnable)
        // Běžící animace drží odkaz na view, proto ji se zničením view zastavíme.
        pulseAnimator?.cancel()
        pulseAnimator = null
        undoSnackbar = null
        _binding = null
    }

    companion object {
        // Konstanty reakčního testu
        private const val NO_RESULT = -1L
        private const val LATE_THRESHOLD_MS = 100L
        private const val KEY_BEST_REACTION = "best_reaction"
    }
}
