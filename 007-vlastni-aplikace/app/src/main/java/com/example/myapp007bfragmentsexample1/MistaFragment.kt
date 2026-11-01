package com.example.myapp007bfragmentsexample1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapp007bfragmentsexample1.databinding.FragmentMistaBinding
import com.example.myapp007bfragmentsexample1.databinding.ItemMistoBinding

// Záložka 2: seznam všech zastavení
class MistaFragment : Fragment() {

    private var _binding: FragmentMistaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMistaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Pro každé místo "nafoukneme" jednu kartu (item_misto.xml) a vložíme ji do seznamu
        for (misto in PruvodceData.mista) {
            val karta = ItemMistoBinding.inflate(layoutInflater, binding.seznamMist, false)

            karta.imgMisto.setImageResource(misto.obrazek)
            karta.txtNazev.text = misto.nazev
            karta.txtLokalita.text = misto.lokalita

            // Klik na kartu otevře detail právě tohoto místa
            karta.root.setOnClickListener {
                (requireActivity() as MainActivity).otevritDetail(misto.id)
            }

            binding.seznamMist.addView(karta.root)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
