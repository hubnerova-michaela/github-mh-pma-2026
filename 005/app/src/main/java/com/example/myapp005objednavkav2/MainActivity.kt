package com.example.myapp005objednavkav2

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapp005objednavkav2.databinding.ActivityMainBinding
import java.text.NumberFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Změna modelu: obrázek + přepočet ceny (lambda)
        binding.rgReader.setOnCheckedChangeListener { _, checkedId ->
            val imageRes = when (checkedId) {
                R.id.rbAndroid -> R.drawable.reader_android
                R.id.rbColor -> R.drawable.reader_color
                else -> R.drawable.reader_kindle
            }
            binding.ivReader.setImageResource(imageRes)
            updatePrice()
        }

        // Změna příslušenství: přepočet ceny (lambda)
        listOf(binding.cbCase, binding.cbLight, binding.cbWarranty).forEach { checkBox ->
            checkBox.setOnCheckedChangeListener { _, _ -> updatePrice() }
        }

        // Odeslání na druhou obrazovku
        binding.btnOrder.setOnClickListener {
            val intent = Intent(this, SummaryActivity::class.java).apply {
                putExtra(SummaryActivity.EXTRA_MODEL, selectedModelName())
                putExtra(SummaryActivity.EXTRA_CASE, binding.cbCase.isChecked)
                putExtra(SummaryActivity.EXTRA_LIGHT, binding.cbLight.isChecked)
                putExtra(SummaryActivity.EXTRA_WARRANTY, binding.cbWarranty.isChecked)
                putExtra(SummaryActivity.EXTRA_PRICE, calculatePrice())
            }
            startActivity(intent)
        }

        updatePrice()   // počáteční cena pro předvybraný model
    }

    private fun selectedModelName(): String = when (binding.rgReader.checkedRadioButtonId) {
        R.id.rbAndroid -> binding.rbAndroid.text.toString()
        R.id.rbColor -> binding.rbColor.text.toString()
        else -> binding.rbKindle.text.toString()
    }

    private fun calculatePrice(): Int {
        var total = when (binding.rgReader.checkedRadioButtonId) {
            R.id.rbAndroid -> resources.getInteger(R.integer.price_android)
            R.id.rbColor -> resources.getInteger(R.integer.price_color)
            else -> resources.getInteger(R.integer.price_kindle)
        }
        if (binding.cbCase.isChecked) total += resources.getInteger(R.integer.price_case)
        if (binding.cbLight.isChecked) total += resources.getInteger(R.integer.price_light)
        if (binding.cbWarranty.isChecked) total += resources.getInteger(R.integer.price_warranty)
        return total
    }

    private fun updatePrice() {
        val money = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("cs-CZ"))
            .format(calculatePrice())
        binding.tvPrice.text = getString(R.string.price_total, money)
    }
}