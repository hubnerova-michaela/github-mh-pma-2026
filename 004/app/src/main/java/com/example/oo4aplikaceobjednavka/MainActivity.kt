package com.example.oo4aplikaceobjednavka

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.oo4aplikaceobjednavka.databinding.ActivityMainBinding
import androidx.appcompat.app.AppCompatDelegate
import java.text.NumberFormat
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var quantity = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(binding.root)

        // systémové lišty
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        // Změna obrázku podle vybraného radio-buttonu
        binding.rgReader.setOnCheckedChangeListener { _, checkedId ->
            val imageRes = when (checkedId) {
                R.id.rbAndroid -> R.drawable.reader_android
                R.id.rbColor -> R.drawable.reader_color
                else -> R.drawable.reader_kindle
            }
            binding.ivReader.setImageResource(imageRes)
        }

        // Tlačítko Objednat -> výpis do souhrnu
        binding.btnOrder.setOnClickListener {
            showSummary()
        }

        // Tlačítko Začít znovu -> reset
        binding.btnReset.setOnClickListener {
            resetOrder()
        }

        binding.cbCase.setOnCheckedChangeListener { _, isChecked ->
            binding.spCaseColor.isEnabled = isChecked
        }

        binding.btnMinus.setOnClickListener {
            if (quantity > 1) {
                quantity--
                binding.tvQuantity.text = quantity.toString()
            }
        }

        binding.btnPlus.setOnClickListener {
            if (quantity < 10) {
                quantity++
                binding.tvQuantity.text = quantity.toString()
            }
        }
    }

    private fun showSummary() {
        var total = 0

        val (model, modelPrice) = when (binding.rgReader.checkedRadioButtonId) {
            R.id.rbAndroid -> binding.rbAndroid.text.toString() to resources.getInteger(R.integer.price_android)
            R.id.rbColor -> binding.rbColor.text.toString() to resources.getInteger(R.integer.price_color)
            else -> binding.rbKindle.text.toString() to resources.getInteger(R.integer.price_kindle)
        }
        total += modelPrice

        val extras = mutableListOf<String>()
        if (binding.cbCase.isChecked) {
            val color = binding.spCaseColor.selectedItem.toString()
            extras.add(getString(R.string.extra_case_color, binding.cbCase.text.toString(), color))
            total += resources.getInteger(R.integer.price_case)
        }
        if (binding.cbLight.isChecked) {
            extras.add(binding.cbLight.text.toString())
            total += resources.getInteger(R.integer.price_light)
        }
        if (binding.cbWarranty.isChecked) {
            extras.add(binding.cbWarranty.text.toString())
            total += resources.getInteger(R.integer.price_warranty)
        }

        val extrasText = if (extras.isEmpty()) {
            getString(R.string.summary_no_extras)
        } else {
            extras.joinToString(", ")
        }

        total *= quantity
        val money = NumberFormat.getCurrencyInstance(Locale("cs", "CZ")).format(total)

        binding.tvSummary.text = getString(R.string.summary_order, model, quantity, extrasText, money)
    }

    private fun resetOrder() {
        quantity = 1
        binding.rgReader.check(R.id.rbKindle)
        binding.cbCase.isChecked = false
        binding.cbLight.isChecked = false
        binding.cbWarranty.isChecked = false
        binding.tvSummary.text = getString(R.string.summary_empty)
        binding.tvQuantity.text = quantity.toString()
        binding.spCaseColor.setSelection(0)
    }
}