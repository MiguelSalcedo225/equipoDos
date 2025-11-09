package com.example.inventorywidget.view.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.inventorywidget.databinding.FragmentAddItemBinding
import com.example.inventorywidget.viewmodel.AddItemViewModel
import com.example.inventorywidget.viewmodel.AddProductViewModelFactory
import com.example.inventorywidget.viewmodel.SaveResult

class AddProductFragment : Fragment() {

    private var _binding: FragmentAddItemBinding? = null
    private val binding get() = _binding!!

    private val addItemViewModel: AddItemViewModel by viewModels {
        AddProductViewModelFactory(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupValidation()
        setupListeners()
        binding.toolbarAddItem.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupListeners() {
        // Botón Guardar
        binding.btnSave.setOnClickListener {
            binding.btnSave.isEnabled = false

            val code = binding.etCode.text.toString()
            val name = binding.etName.text.toString()
            val price = binding.etPrice.text.toString()
            val quantity = binding.etQuantity.text.toString()

            addItemViewModel.saveInventory(code, name, price, quantity)
        }

        addItemViewModel.saveResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is SaveResult.Success -> {
                    Toast.makeText(requireContext(), "Producto guardado correctamente", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                }
                is SaveResult.Error -> {
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_LONG).show()
                    binding.btnSave.isEnabled = true
                }
                null -> {}
            }
        }
    }

    private fun setupValidation() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updateSaveButtonState()
            }
        }

        binding.etCode.addTextChangedListener(watcher)
        binding.etName.addTextChangedListener(watcher)
        binding.etPrice.addTextChangedListener(watcher)
        binding.etQuantity.addTextChangedListener(watcher)
    }

    private fun updateSaveButtonState() {
        val valid = binding.etCode.text!!.isNotBlank() &&
                binding.etName.text!!.isNotBlank() &&
                binding.etPrice.text!!.isNotBlank() &&
                binding.etQuantity.text!!.isNotBlank()
        binding.btnSave.isEnabled = valid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
