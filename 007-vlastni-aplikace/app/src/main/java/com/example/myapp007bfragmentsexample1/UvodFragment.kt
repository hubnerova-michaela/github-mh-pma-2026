package com.example.myapp007bfragmentsexample1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapp007bfragmentsexample1.databinding.FragmentUvodBinding

// Záložka 1: kdo byl doktor Kittel
class UvodFragment : Fragment() {

    // ViewBinding ve fragmentu: binding existuje jen dokud existuje vzhled (View) fragmentu
    private var _binding: FragmentUvodBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUvodBinding.inflate(inflater, container, false)
        return binding.root
    }

    // Sem patří práce s tlačítky a texty (vzhled už je hotový)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnProzkoumat.setOnClickListener {
            (requireActivity() as MainActivity).prepnoutNaMista()
        }
    }

    // Když fragment zmizí z obrazovky, uvolníme binding (prevence úniku paměti)
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
