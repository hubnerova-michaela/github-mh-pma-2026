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
import android.view.animation.DecelerateInterpolator
import android.graphics.Color
import androidx.activity.SystemBarStyle

class MainActivity : AppCompatActivity() {

    private val diceSymbols = listOf("⚀", "⚁", "⚂", "⚃", "⚄", "⚅")

    // Handler na hlavním vlákně pro odložené spouštění (prodleva 250 ms)
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var tvDice: TextView
    private lateinit var btnRoll: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Světlé pozadí => tmavé ikony v systémových lištách (i v tmavém režimu telefonu)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )
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

    // Spustí hod: otočení kostky + 10 náhodných změn po 250 ms + výsledný hod
    private fun rollDice() {
        btnRoll.isEnabled = false                // zákaz tlačítka během animace

        // Animace otočení: 3 celé otáčky (1080°) za 2500 ms, postupně zpomaluje
        tvDice.animate()
            .rotationBy(1080f)
            .setDuration(2500)
            .setInterpolator(DecelerateInterpolator())
            .start()

        showRandomSymbol(stepsLeft = 10)
    }

    // Zobrazí 10 náhodných symbolů, poté výsledný hod
    private fun showRandomSymbol(stepsLeft: Int) {
        tvDice.text = diceSymbols.random()       // ruční změna textu

        if (stepsLeft > 0) {
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