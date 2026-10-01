package com.example.dicexml

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private val diceSymbols = listOf("⚀", "⚁", "⚂", "⚃", "⚄", "⚅")

    // Handler na hlavním vlákně pro odložené spouštění (prodleva 250 ms)
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var tvDice: TextView
    private lateinit var btnRoll: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()                       // obsah pod systémovými lištami
        setContentView(R.layout.activity_main)

        // Odsazení obsahu od systémových lišt
        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.llMain)
        ) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        // Přístup k prvkům pomocí findViewById
        tvDice = findViewById(R.id.tvDice)
        btnRoll = findViewById(R.id.btnRoll)

        // Kliknutí na tlačítko spustí hod
        btnRoll.setOnClickListener { rollDice() }
    }

    // Spustí animaci: 10 náhodných změn po 250 ms
    private fun rollDice() {
        btnRoll.isEnabled = false                // zákaz tlačítka během animace
        showRandomSymbol(stepsLeft = 10)
    }

    // Rekurzivně zobrazuje náhodné symboly, dokud zbývají kroky
    private fun showRandomSymbol(stepsLeft: Int) {
        // Přímá změna textu v TextView
        tvDice.text = diceSymbols.random()

        if (stepsLeft > 1) {
            // Naplánuje další krok za 250 ms
            handler.postDelayed({ showRandomSymbol(stepsLeft - 1) }, 250)
        } else {
            btnRoll.isEnabled = true             // konec animace, výsledný hod zůstává
        }
    }

    // Zrušení čekajících úloh při zničení aktivity
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}