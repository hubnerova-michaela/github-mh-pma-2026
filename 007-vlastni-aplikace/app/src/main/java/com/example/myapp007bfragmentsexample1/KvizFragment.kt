package com.example.myapp007bfragmentsexample1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.myapp007bfragmentsexample1.databinding.FragmentKvizBinding

// Záložka 4: kvíz "Pravda, nebo pověst?"
class KvizFragment : Fragment() {

    private var _binding: FragmentKvizBinding? = null
    private val binding get() = _binding!!

    private val otazky = PruvodceData.kviz
    private var index = 0          // kolikátá otázka je na řadě
    private var skore = 0          // kolik odpovědí bylo správně
    private var odpovezeno = false // už uživatel na aktuální otázku odpověděl?
    private var jeKonec = false    // je zrovna zobrazená obrazovka s výsledkem?
    private var kvizSkoncil = false // právě se ukazuje závěrečná obrazovka se skóre?

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKvizBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Po otočení displeje obnovíme rozehranou hru
        if (savedInstanceState != null) {
            index = savedInstanceState.getInt("index")
            skore = savedInstanceState.getInt("skore")
            odpovezeno = false // otázku ukážeme znovu bez vyhodnocení
        }

        binding.btnPravda.setOnClickListener { odpovedet(true) }
        binding.btnPovest.setOnClickListener { odpovedet(false) }
        binding.btnDalsi.setOnClickListener {
            if (jeKonec) {
                // Tlačítko "Hrát znovu" na obrazovce s výsledkem -> nová hra
                index = 0
                skore = 0
            }
            // Jinak: další otázka, nebo (po poslední otázce) obrazovka s výsledkem
            zobrazitOtazku()
        }

        zobrazitOtazku()
    }

    private fun zobrazitOtazku() {
        if (index >= otazky.size) {
            zobrazitKonec()
            return
        }
        odpovezeno = false
        jeKonec = false
        binding.txtPocitadlo.text = getString(R.string.kviz_pocitadlo, index + 1, otazky.size)
        binding.txtTvrzeni.text = otazky[index].tvrzeni
        binding.txtVysledek.visibility = View.GONE
        binding.btnDalsi.visibility = View.GONE
        binding.layoutOdpovedi.visibility = View.VISIBLE
    }

    private fun odpovedet(tipPravda: Boolean) {
        if (odpovezeno) return
        odpovezeno = true

        val otazka = otazky[index]
        val spravne = (tipPravda == otazka.jePravda)
        if (spravne) skore++

        binding.txtVysledek.text = if (spravne) {
            getString(R.string.kviz_spravne, otazka.vysvetleni)
        } else {
            getString(R.string.kviz_spatne, otazka.vysvetleni)
        }
        binding.txtVysledek.setTextColor(
            ContextCompat.getColor(requireContext(), if (spravne) R.color.les else R.color.dabel)
        )
        binding.txtVysledek.visibility = View.VISIBLE

        index++
        binding.btnDalsi.text = if (index >= otazky.size) {
            getString(R.string.kviz_vysledky)
        } else {
            getString(R.string.kviz_dalsi)
        }
        binding.btnDalsi.visibility = View.VISIBLE
        binding.layoutOdpovedi.visibility = View.GONE
    }

    private fun zobrazitKonec() {
        jeKonec = true
        binding.txtPocitadlo.text = getString(R.string.kviz_konec)
        binding.txtTvrzeni.text = getString(R.string.kviz_skore, skore, otazky.size)
        binding.txtVysledek.text = when {
            skore == otazky.size -> getString(R.string.kviz_hodnoceni_1)
            skore >= otazky.size / 2 -> getString(R.string.kviz_hodnoceni_2)
            else -> getString(R.string.kviz_hodnoceni_3)
        }
        binding.txtVysledek.setTextColor(ContextCompat.getColor(requireContext(), R.color.inkoust))
        binding.txtVysledek.visibility = View.VISIBLE
        binding.layoutOdpovedi.visibility = View.GONE
        binding.btnDalsi.text = getString(R.string.kviz_znovu)
        binding.btnDalsi.visibility = View.VISIBLE
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Pokud už byla otázka zodpovězená, index je posunutý -> uložíme ten posunutý
        outState.putInt("index", index)
        outState.putInt("skore", skore)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}