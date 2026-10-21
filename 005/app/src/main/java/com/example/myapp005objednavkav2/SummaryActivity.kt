package com.example.myapp005objednavkav2

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapp005objednavkav2.databinding.ActivitySummaryBinding
import java.text.NumberFormat
import java.util.Locale

class SummaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySummaryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Příjem dat z Intentu
        val model = intent.getStringExtra(EXTRA_MODEL).orEmpty()
        val hasCase = intent.getBooleanExtra(EXTRA_CASE, false)
        val hasLight = intent.getBooleanExtra(EXTRA_LIGHT, false)
        val hasWarranty = intent.getBooleanExtra(EXTRA_WARRANTY, false)
        val price = intent.getIntExtra(EXTRA_PRICE, 0)

        // Sestavení přehledu
        val extras = mutableListOf<String>()
        if (hasCase) extras.add(getString(R.string.summary_extra_item, getString(R.string.extra_case)))
        if (hasLight) extras.add(getString(R.string.summary_extra_item, getString(R.string.extra_light)))
        if (hasWarranty) extras.add(getString(R.string.summary_extra_item, getString(R.string.extra_warranty)))

        val extrasText = if (extras.isEmpty()) {
            getString(R.string.summary_no_extras)
        } else {
            extras.joinToString("\n")
        }

        val money = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("cs-CZ")).format(price)

        binding.tvSelectedBike.text = getString(R.string.summary_model, model)
        binding.tvExtras.text = getString(R.string.summary_extras, extrasText)
        binding.tvFinalPrice.text = getString(R.string.price_total, money)

        binding.btnBack.setOnClickListener { finish() }
    }

    companion object {
        const val EXTRA_MODEL = "extra_model"
        const val EXTRA_CASE = "extra_case"
        const val EXTRA_LIGHT = "extra_light"
        const val EXTRA_WARRANTY = "extra_warranty"
        const val EXTRA_PRICE = "extra_price"
    }
}