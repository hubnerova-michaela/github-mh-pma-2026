package com.example.myapp007bfragmentsexample1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapp007bfragmentsexample1.databinding.FragmentPovestiBinding
import com.example.myapp007bfragmentsexample1.databinding.ItemPovestBinding

// Záložka 3: pověsti o doktoru Kittelovi. Klik na nadpis pověst rozbalí / sbalí.
class PovestiFragment : Fragment() {

    private var _binding: FragmentPovestiBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPovestiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        for ((nadpis, text) in PruvodceData.povesti) {
            val karta = ItemPovestBinding.inflate(layoutInflater, binding.seznamPovesti, false)
            karta.txtNadpis.text = nadpis
            karta.txtText.text = text

            karta.root.setOnClickListener {
                val rozbalit = karta.txtText.visibility != View.VISIBLE
                karta.txtText.visibility = if (rozbalit) View.VISIBLE else View.GONE
                karta.txtSipka.text = if (rozbalit) "▲" else "▼"
            }

            binding.seznamPovesti.addView(karta.root)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
