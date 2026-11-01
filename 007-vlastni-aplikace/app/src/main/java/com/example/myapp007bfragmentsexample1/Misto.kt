package com.example.myapp007bfragmentsexample1

import androidx.annotation.DrawableRes

// Jedno zastavení v průvodci.
// Díky "data class" stačí přidat další místo jedním řádkem do seznamu v MistaData.kt.
data class Misto(
    val id: Int,                 // podle id pozná DetailFragment, které místo má ukázat
    val nazev: String,
    val lokalita: String,        // část obce / okolí
    @DrawableRes val obrazek: Int,
    val popis: String,
    val zajimavost: String,
    val povest: String?,         // null = k místu žádná pověst není, tlačítko se skryje
    val hledatVMapach: String    // text, který se pošle do aplikace Mapy
)
