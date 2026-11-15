package com.example.myapp008asharedpref

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
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myapp008asharedpref.databinding.FragmentSettingsBinding
import kotlin.random.Random

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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Při otevření Nastavení předvyplníme formulář uloženými hodnotami.
        // Po otočení displeje je přepíše obnovený stav prvků, takže se rozepsané změny neztratí.
        binding.etNickname.setText(prefs.getString(KEY_NICKNAME, ""))
        binding.switchHardcoreMode.isChecked = prefs.getBoolean(KEY_HARDCORE_MODE, false)

        // Reakční test s ukládáním nesouvisí, a přesto ho Thread.sleep rozbije. Zelenou zobrazuje
        // Runnable naplánovaný přes postDelayed a klepnutí zpracovává listener, obojí na hlavním
        // vlákně. Když je vlákno 3 s uspané, zelená naskočí pozdě a klepnutí čekají ve frontě.
        // Test proto ukáže, o kolik ms se zelená opozdila. Pulzování pole navíc zamrzne uprostřed
        // pohybu, protože i snímky animace počítá hlavní vlákno. Kolečko se přitom točí dál
        // (kreslí ho RenderThread), takže skutečné zaseknutí ukazuje až tenhle prvek.
        setupReactionTest(savedInstanceState)

        binding.btnSave.setOnClickListener {
            // Listener běží na hlavním (UI) vlákně. To jediné vlákno zpracovává dotyky
            // i překreslování obrazovky, a to postupně, jednu událost po druhé.
            // Thread.sleep ho uspí přímo tady, takže se další událost dostane na řadu
            // až po 3 sekundách: aplikace nereaguje a dotyky čekají ve frontě, proto se
            // třeba klik na přepínač provede opožděně, až po uložení.
            // Takhle by se aplikace chovala při opravdu pomalém zápisu na disk.
            // Pozor: kolečko se točí dál, protože jeho animaci kreslí samostatné
            // RenderThread vlákno. Točící se kolečko tedy nedokazuje, že UI vlákno běží.
            Thread.sleep(3000)

            // commit() zapisuje synchronně: vrátí se až po zápisu na disk a řekne, jestli se
            // zápis povedl. Asynchronní apply() žádný výsledek nevrací, proto ho tu nepoužijeme.
            val saved = prefs.edit()
                .putString(KEY_NICKNAME, binding.etNickname.text.toString())
                .putBoolean(KEY_HARDCORE_MODE, binding.switchHardcoreMode.isChecked)
                .commit()

            val message = if (saved) "Profil uložen" else "Profil se nepodařilo uložit"
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
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
        _binding = null
    }

    // Název souboru a klíče sdílí oba fragmenty, proto jsou na jednom místě.
    companion object {
        // Konstanty reakčního testu
        private const val NO_RESULT = -1L
        private const val LATE_THRESHOLD_MS = 100L
        private const val KEY_BEST_REACTION = "best_reaction"

        const val PREFS_NAME = "player_profile"
        const val KEY_NICKNAME = "nickname"
        const val KEY_HARDCORE_MODE = "hardcore_mode"
    }
}
