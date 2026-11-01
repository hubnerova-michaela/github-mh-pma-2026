package com.example.myapp007bfragmentsexample1

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.myapp007bfragmentsexample1.databinding.FragmentDetailBinding

// Detail jednoho místa. Je jen JEDEN fragment pro všechna místa,
// které místo má ukázat, se dozví z argumentů (Bundle).
class DetailFragment : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_ID_MISTA = "id_mista"

        // Správný způsob, jak fragmentu předat data: uložit je do arguments.
        // (Android si je pamatuje i po otočení displeje.)
        fun novaInstance(idMista: Int): DetailFragment {
            val fragment = DetailFragment()
            fragment.arguments = Bundle().apply {
                putInt(ARG_ID_MISTA, idMista)
            }
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Vytáhneme id z argumentů a najdeme podle něj místo
        val idMista = requireArguments().getInt(ARG_ID_MISTA)
        val misto = PruvodceData.najdiMisto(idMista) ?: return

        binding.imgDetail.setImageResource(misto.obrazek)
        binding.txtNazev.text = misto.nazev
        binding.txtLokalita.text = misto.lokalita
        binding.txtPopis.text = misto.popis
        binding.txtZajimavost.text = misto.zajimavost

        // Pověst: pokud k místu žádná není, tlačítko vůbec neukážeme
        if (misto.povest == null) {
            binding.btnPovest.visibility = View.GONE
        } else {
            binding.txtPovest.text = misto.povest
            binding.btnPovest.setOnClickListener {
                if (binding.kartaPovest.visibility == View.VISIBLE) {
                    binding.kartaPovest.visibility = View.GONE
                    binding.btnPovest.text = getString(R.string.ukazat_povest)
                } else {
                    binding.kartaPovest.visibility = View.VISIBLE
                    binding.btnPovest.text = getString(R.string.skryt_povest)
                }
            }
        }

        // Navigace: otevře místo v aplikaci s mapami (Mapy.cz, Google Maps...)
        binding.btnNavigovat.setOnClickListener {
            val adresa = Uri.parse("geo:0,0?q=" + Uri.encode(misto.hledatVMapach, ",.()"))
            try {
                startActivity(Intent(Intent.ACTION_VIEW, adresa))
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(requireContext(), R.string.chyba_mapy, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
