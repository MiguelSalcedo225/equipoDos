package com.example.inventorywidget.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.inventorywidget.R
import com.example.inventorywidget.databinding.FragmentItemDetailsBinding
import com.example.inventorywidget.model.Product
import com.example.inventorywidget.viewmodel.InventoryViewModel

class ItemDetailsFragment : Fragment() {
    private lateinit var binding: FragmentItemDetailsBinding
    private val inventoryViewModel: InventoryViewModel by viewModels()
    private lateinit var receivedProduct: Product

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentItemDetailsBinding.inflate(inflater)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataInventory()
        controladores()
    }

    private fun controladores() {
        binding.btnDelete.setOnClickListener {
            deleteInventory()
        }

        binding.fbEdit.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("dataInventory", receivedProduct)
            findNavController().navigate(R.id.action_detailFragment_to_editFragment, bundle)
        }
    }

    private fun dataInventory() {
        val receivedBundle = arguments
        receivedProduct = receivedBundle?.getSerializable("clave") as Product
        binding.tvItem.text = "${receivedProduct.name}"
        binding.tvPrice.text = "$ ${receivedProduct.unitPrice}"
        binding.tvQuantity.text = "${receivedProduct.quantity}"
        binding.txtTotal.text = "$ ${
            inventoryViewModel.totalProducto(
                receivedProduct.unitPrice,
                receivedProduct.quantity
            )
        }"
    }

    private fun deleteInventory(){
        inventoryViewModel.deleteInventory(receivedProduct)
        findNavController().popBackStack()
    }

}