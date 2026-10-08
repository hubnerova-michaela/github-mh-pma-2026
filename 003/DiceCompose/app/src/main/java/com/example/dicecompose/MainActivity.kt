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
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.ui.graphics.graphicsLayer
// --- nové importy pro design ---
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import com.example.dicecompose.ui.theme.CreamCard
import com.example.dicecompose.ui.theme.DeepGreen
import com.example.dicecompose.ui.theme.MintBackground
import com.example.dicecompose.ui.theme.SageDisabled
import com.example.dicecompose.ui.theme.SageGreen

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
    // Úhel otočení kostky; Animatable umí hodnotu plynule animovat
    val rotation = remember { Animatable(0f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MintBackground)          // mátové pozadí (před paddingem, aby šlo pod lišty)
            .systemBarsPadding(),                // odsazení od systémových lišt
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center // vystředění obsahu
    ) {
        // Nadpis v rukopisném písmu
        Text(
            text = "🌿 Hoď kostkou 🌿",
            fontSize = 36.sp,
            fontFamily = FontFamily.Cursive,
            fontWeight = FontWeight.Bold,
            color = DeepGreen
        )

        // Karta s kostkou: krémové zaoblené pozadí + šalvějový obrys
        Box(
            modifier = Modifier
                .padding(vertical = 24.dp)
                .background(CreamCard, RoundedCornerShape(40.dp))
                .border(3.dp, SageGreen, RoundedCornerShape(40.dp))
                .padding(horizontal = 40.dp, vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            // Symbol kostky, zobrazuje aktuální hodnotu stavu
            Text(
                text = diceSymbol,
                fontSize = 140.sp,
                modifier = Modifier
                    .padding(16.dp)                                  // rezerva, aby se při otáčení neořezávalo
                    .graphicsLayer { rotationZ = rotation.value },   // otočení podle stavu
                color = DeepGreen
            )
        }

        // Tlačítko; enabled se řídí stavem isRolling
        Button(
            enabled = !isRolling,
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SageGreen,
                contentColor = DeepGreen,
                disabledContainerColor = SageDisabled,
                disabledContentColor = DeepGreen.copy(alpha = 0.5f)
            ),
            onClick = {
                scope.launch {
                    isRolling = true                     // zákaz tlačítka

                    // Animace otočení běží souběžně (vlastní coroutine)
                    launch {
                        rotation.snapTo(0f)              // začni od nuly
                        rotation.animateTo(
                            targetValue = 1080f,         // 3 celé otáčky
                            animationSpec = tween(
                                durationMillis = 2500,
                                easing = LinearOutSlowInEasing  // na konci zpomalí
                            )
                        )
                    }

                    repeat(10) {                         // 10 náhodných změn
                        diceSymbol = diceSymbols.random()
                        delay(250)                       // prodleva 250 ms
                    }
                    diceSymbol = diceSymbols.random()    // výsledný hod
                    isRolling = false                    // povolení tlačítka
                }
            }
        ) {
            Text("Hodit 🌱", fontSize = 20.sp)
        }

        // Dekorace
        Text(
            text = "🪴 🌱 🌿 🌱 🪴",
            fontSize = 28.sp,
            modifier = Modifier.padding(top = 32.dp)
        )
    }
}