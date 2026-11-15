package com.example.myapp008asharedpref

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapp008asharedpref.databinding.FragmentDashboardBinding

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // addToBackStack zajistí, že se tlačítkem Zpět vrátíme z Nastavení na Dashboard.
        binding.btnEditProfile.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, SettingsFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    // onResume běží při každém zobrazení Dashboardu (po spuštění, po návratu z Nastavení
    // i z jiné aplikace), takže tu jsou vždy aktuální hodnoty.
    override fun onResume() {
        super.onResume()
        loadProfile()
    }

    private fun loadProfile() {
        val prefs = requireContext().getSharedPreferences(
            SettingsFragment.PREFS_NAME, Context.MODE_PRIVATE
        )
        val nickname = prefs.getString(SettingsFragment.KEY_NICKNAME, null)
        val hardcoreMode = prefs.getBoolean(SettingsFragment.KEY_HARDCORE_MODE, false)

        binding.tvNickname.text =
            "Přezdívka: ${if (nickname.isNullOrBlank()) "zatím nenastavena" else nickname}"
        binding.tvHardcoreMode.text =
            "Hardcore režim: ${if (hardcoreMode) "zapnutý" else "vypnutý"}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
