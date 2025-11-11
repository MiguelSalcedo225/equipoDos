package com.example.inventorywidget.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.inventorywidget.R
import com.example.inventorywidget.databinding.FragmentItemEditBinding
import com.example.inventorywidget.model.Inventory
import com.example.inventorywidget.viewmodel.InventoryViewModel

class ItemEditFragment : Fragment() {

    private lateinit var binding: FragmentItemEditBinding
    private val inventoryViewModel: InventoryViewModel by viewModels()
    private lateinit var receivedInventory: Inventory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentItemEditBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupData()
        setupListeners()
    }

    private fun setupData() {
        receivedInventory = requireArguments().getSerializable("clave") as Inventory

        binding.etId.setText(getString(R.string.inventory_id, receivedInventory.id))
        binding.etName.setText(receivedInventory.name)
        binding.etPrice.setText(receivedInventory.price.toString())
        binding.etQuantity.setText(receivedInventory.quantity.toString())

        validateFields()
    }

    private fun setupListeners() {

        binding.toolbarEdit.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.etName.addTextChangedListener { validateFields() }
        binding.etPrice.addTextChangedListener { validateFields() }
        binding.etQuantity.addTextChangedListener { validateFields() }

        binding.btnEdit.setOnClickListener {
            updateInventory()
        }
    }

    private fun validateFields() {
        val name = binding.etName.text.toString().trim()
        val price = binding.etPrice.text.toString().trim()
        val quantity = binding.etQuantity.text.toString().trim()

        val valid = name.isNotEmpty() && price.isNotEmpty() && quantity.isNotEmpty()
        binding.btnEdit.isEnabled = valid
    }

    private fun updateInventory() {
        val name = binding.etName.text.toString().trim()
        val priceStr = binding.etPrice.text.toString().trim()
        val quantityStr = binding.etQuantity.text.toString().trim()


        if (name.length > 40) {
            Toast.makeText(requireContext(), "El nombre no puede superar 40 caracteres", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val price = priceStr.toInt()
            val quantity = quantityStr.toInt()

            if (priceStr.length > 20 || quantityStr.length > 4) {
                Toast.makeText(requireContext(), "Precio o cantidad exceden el máximo permitido", Toast.LENGTH_SHORT).show()
                return
            }

            val updatedInventory = Inventory(
                id = receivedInventory.id,
                name = name,
                price = price,
                quantity = quantity
            )

            inventoryViewModel.updateInventory(updatedInventory)
            Toast.makeText(requireContext(), "Producto actualizado correctamente", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_itemEditFragment_to_homeInventoryFragment)

        } catch (_: NumberFormatException) {
            Toast.makeText(requireContext(), "Verifique los valores numéricos", Toast.LENGTH_SHORT).show()
        }
    }
}
