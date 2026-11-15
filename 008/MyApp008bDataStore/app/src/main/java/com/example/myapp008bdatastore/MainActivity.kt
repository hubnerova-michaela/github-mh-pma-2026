package com.example.myapp008bdatastore

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

// Aktivita je jen obálka: drží prázdný kontejner, do kterého se vkládají fragmenty.
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Pokud se aplikace zapíná poprvé, načteme DashboardFragment.
        // Po otočení displeje obnoví fragmenty sám FragmentManager, takže by vložení
        // navíc zahodilo aktuální obrazovku (např. rozepsané Nastavení).
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, DashboardFragment())
                .commit()
        }
    }
}
