package com.example.myapp007bfragmentsexample1

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.myapp007bfragmentsexample1.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    // ViewBinding pro activity_main.xml (stejně jako v MyApp007aFragments)
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Aby obsah nezajel pod stavový řádek nahoře.
        // (Místo dole pod systémovou lištou si dolní navigace odsadí sama.)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(bars.left, bars.top, bars.right, 0)
            insets
        }

        // Dolní navigace: místo dvou tlačítek z části A máme čtyři záložky
        binding.bottomNav.setOnItemSelectedListener { polozka ->
            when (polozka.itemId) {
                R.id.nav_uvod -> zobrazitFragment(UvodFragment())
                R.id.nav_mista -> zobrazitFragment(MistaFragment())
                R.id.nav_povesti -> zobrazitFragment(PovestiFragment())
                R.id.nav_kviz -> zobrazitFragment(KvizFragment())
            }
            true // true = záložka se má označit jako vybraná
        }

        // Klik na už vybranou záložku (např. z detailu místa zpět na seznam)
        binding.bottomNav.setOnItemReselectedListener {
            supportFragmentManager.popBackStack()
        }

        // Při prvním spuštění ukážeme úvod (při otočení displeje si ho Android obnoví sám)
        // (Záložka Úvod je v liště vybraná už od začátku, proto fragment vložíme přímo.)
        if (savedInstanceState == null) {
            zobrazitFragment(UvodFragment())
        }
    }

    // Přepnutí záložky: vymění obsah kontejneru, úplně stejně jako v části A
    private fun zobrazitFragment(fragment: Fragment) {
        // Nejdřív zahodíme otevřené detaily, aby tlačítko Zpět nevracelo do jiné záložky
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    // Otevření detailu místa. Na rozdíl od záložek si ho uložíme do "back stacku",
    // takže systémové tlačítko Zpět vrátí uživatele na seznam místo zavření aplikace.
    fun otevritDetail(idMista: Int) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, DetailFragment.novaInstance(idMista))
            .addToBackStack(null)
            .commit()
    }

    // Úvodní obrazovka tímto přepne na záložku Zastavení
    fun prepnoutNaMista() {
        binding.bottomNav.selectedItemId = R.id.nav_mista
    }
}
