package com.example.inventorywidget.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.inventorywidget.R
import com.example.inventorywidget.databinding.FragmentItemEditBinding
import com.example.inventorywidget.model.Product
import com.example.inventorywidget.viewmodel.InventoryViewModel

class ItemEditFragment : Fragment() {
    private lateinit var binding: FragmentItemEditBinding
    private val inventoryViewModel: InventoryViewModel by viewModels()
    private lateinit var receivedProduct: Product

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentItemEditBinding.inflate(inflater)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataInventory()
        controladores()

    }

    private fun controladores(){
        binding.btnEdit.setOnClickListener {
            updateInventory()
        }
    }

    private fun dataInventory(){
        val receivedBundle = arguments
        receivedProduct = receivedBundle?.getSerializable("dataInventory") as Product
        binding.etName.setText(receivedProduct.name)
        binding.etPrice.setText(receivedProduct.unitPrice.toString())
        binding.etQuantity.setText(receivedProduct.quantity.toString())

    }

    private fun updateInventory(){
        val name = binding.etName.text.toString()
        val price = binding.etPrice.text.toString().toDouble()
        val quantity = binding.etQuantity.text.toString().toInt()
        val product = Product(receivedProduct.code, name, price, quantity)
        inventoryViewModel.updateInventory(product)
        findNavController().navigate(R.id.action_itemEditFragment_to_homeInventoryFragment)

    }
}