package com.example.myapp008bdatastore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.myapp008bdatastore.databinding.FragmentDashboardBinding
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    // Stejné klíče jako v SettingsFragment: textové názvy se musí shodovat.
    private val NICKNAME_KEY = stringPreferencesKey("NICKNAME")
    private val HARDCORE_KEY = booleanPreferencesKey("HARDCORE")

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

        // Rozdíl oproti části A: tam jsme si data v onResume sami načetli jednou. Flow je
        // proud hodnot, takže každá změna v DataStore sama dorazí sem a text se aktualizuje.
        // repeatOnLifecycle sbírání spustí při STARTED a při zastavení obrazovky ho zruší,
        // takže na pozadí zbytečně neběží.
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                requireContext().dataStore.data.collect { preferences ->
                    val nickname = preferences[NICKNAME_KEY]
                    val hardcoreMode = preferences[HARDCORE_KEY] ?: false

                    binding.tvNickname.text =
                        "Přezdívka: ${if (nickname.isNullOrBlank()) "zatím nenastavena" else nickname}"
                    binding.tvHardcoreMode.text =
                        "Hardcore režim: ${if (hardcoreMode) "zapnutý" else "vypnutý"}"
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
