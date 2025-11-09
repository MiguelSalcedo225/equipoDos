package com.example.inventorywidget.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.inventorywidget.R
import com.example.inventorywidget.databinding.FragmentHomeBinding
import com.example.inventorywidget.viewmodel.InventoryViewModel
import java.text.NumberFormat
import java.util.*

class HomeInventoryFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val inventoryViewModel: InventoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_homeInventoryFragment_to_addItemFragment)
        }

        inventoryViewModel.totalInventoryValue.observe(viewLifecycleOwner) { total ->
            val formattedTotal = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
                .format(total ?: 0.0)
            binding.tvTotalValue.text = "Saldo Total: $formattedTotal"
        }

        inventoryViewModel.getListInventory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

