package com.example.frafmentsapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.frafmentsapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            zobrazitFragment(FragmentA())
        }

        binding.btnFragmentA.setOnClickListener { zobrazitFragment(FragmentA()) }
        binding.btnFragmentB.setOnClickListener { zobrazitFragment(FragmentB()) }
    }

    private fun zobrazitFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}