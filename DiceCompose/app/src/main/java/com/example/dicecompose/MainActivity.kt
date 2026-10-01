package com.example.dicecompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dicecompose.ui.theme.DiceComposeTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()                       // obsah pod systémovými lištami
        setContent {
            DiceComposeTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    DiceScreen()
                }
            }
        }
    }
}

// Unicode symboly pro hodnoty 1–6
private val diceSymbols = listOf("⚀", "⚁", "⚂", "⚃", "⚄", "⚅")

@Composable
fun DiceScreen() {
    // Stav: když se změní, Compose automaticky překreslí UI
    var diceSymbol by remember { mutableStateOf(diceSymbols.first()) }
    var isRolling by remember { mutableStateOf(false) }

    // Scope pro coroutine (potřebujeme delay)
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding(),                // odsazení od systémových lišt
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center // vystředění obsahu
    ) {
        // Nadpis
        Text(
            text = "Hoď kostkou",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        // Symbol kostky, zobrazuje aktuální hodnotu stavu
        Text(
            text = diceSymbol,
            fontSize = 160.sp,
            modifier = Modifier.padding(vertical = 24.dp),
            color = MaterialTheme.colorScheme.onBackground
        )

        // Tlačítko; enabled se řídí stavem isRolling
        Button(
            enabled = !isRolling,
            onClick = {
                scope.launch {
                    isRolling = true             // zákaz tlačítka
                    repeat(10) {                 // 10 náhodných změn
                        diceSymbol = diceSymbols.random()
                        delay(250.milliseconds)               // prodleva 250 ms
                    }
                    diceSymbol = diceSymbols.random() // výsledný hod
                    isRolling = false            // povolení tlačítka
                }
            }
        ) {
            Text("Hodit")
        }
    }
}