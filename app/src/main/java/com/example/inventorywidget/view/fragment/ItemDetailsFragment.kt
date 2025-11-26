package com.example.inventorywidget.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.inventorywidget.R
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.inventorywidget.databinding.FragmentItemDetailsBinding
import java.text.NumberFormat
import java.util.*
import com.example.inventorywidget.viewmodel.DetailViewModel
import com.example.inventorywidget.viewmodel.DetailViewModelFactory

/**
 * Fragment que muestra los detalles de un producto específico
 * Permite ver la información completa del producto y editar si es necesario
 */
class ItemDetailsFragment : Fragment() {

    private var _binding: FragmentItemDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DetailViewModel by viewModels {
        DetailViewModelFactory(requireActivity().application)
    }

    private val args: ItemDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        setupClickListeners()
        loadProductDetail()
    }

    /**
     * Carga los detalles del producto basado en el código recibido
     */
    private fun loadProductDetail() {
        val Id = args.productCode
        viewModel.loadProduct(Id)
    }

    /**
     * Configura los observadores del ViewModel
     */
    private fun setupObservers() {
        viewModel.product.observe(viewLifecycleOwner) { product ->
            if (product != null) {
                // Llenar los campos con la información del producto
                binding.tvName.text = product.name
                binding.tvPrice.text = formatCurrency(product.unitPrice)
                binding.tvQuantity.text = product.quantity.toString()

            }
        }

        viewModel.totalInventoryPrice.observe(viewLifecycleOwner) { totalInventoryPrice ->

            totalInventoryPrice?.let {
                binding.txtTotal.text=formatCurrency(it)
            }
            }



//        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
//            if (errorMessage != null) {
//                // Mostrar un mensaje de error
//                binding.tvErrorMessage.visibility = View.VISIBLE
//                binding.tvErrorMessage.text = errorMessage
//            }
//        }
    }

    /**
     * Configura los listeners de los botones
     */
    private fun setupClickListeners() {

        binding.btnDelete.setOnClickListener {
            val Id = args.productCode
            AlertDialog.Builder(requireContext())
                .setTitle("Eliminar Producto")
                .setMessage("¿Estás seguro de que deseas Eliminar el Producto?")
                .setPositiveButton("Sí") { _, _ ->
                    viewModel.deleteProduct(Id)
                    findNavController().navigateUp()
                }
                .setNegativeButton("Cancelar", null)
                .show()


        }

        binding.fbEdit.setOnClickListener {

            val currentProduct=viewModel.product.value

            if(currentProduct!=null){
                val bundle=Bundle()

                bundle.putSerializable("dataInventory",currentProduct)

                findNavController().navigate(R.id.action_itemDetailsFragment_to_itemEditFragment,bundle)

            }
            else{
                findNavController().navigateUp()
            }

        }

    }



    /**
     * Formatea un número como moneda
     */
    private fun formatCurrency(value: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
        return format.format(value)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
