package com.example.shopmoz.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopmoz.R
import com.example.shopmoz.adapters.CartProductsAdapter
import com.example.shopmoz.databinding.FragmentCartBinding
import com.example.shopmoz.firebase.FirebaseCommon
import com.example.shopmoz.util.Resource
import com.example.shopmoz.util.VerticaltemDecoration
import com.example.shopmoz.viewmodel.CartViewModel
import kotlinx.coroutines.flow.collectLatest

class CartFragment: Fragment(R.layout.fragment_cart) {

    private lateinit var binding: FragmentCartBinding
    private val cartAdapter by lazy { CartProductsAdapter() }
    private val viewModel by activityViewModels<CartViewModel> ()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCartRV()

        lifecycleScope.launchWhenStarted {
            viewModel.productsPrice.collectLatest { price ->
                price?.let {
                    binding.tvTotal.text="MZ $price"
                }
            }
        }

        cartAdapter.onProductClick = {
            val b = Bundle().apply { putParcelable("product",it.product) }
            findNavController().navigate(R.id.action_cartFragment_to_productDetailsFragment, b)
        }

        cartAdapter.onPlustClick ={
            viewModel.changeQuantity(it, FirebaseCommon.QuantityChanging.INCREASE)
        }

        cartAdapter.onMinusClick ={
            viewModel.changeQuantity(it, FirebaseCommon.QuantityChanging.DESCREASE)
        }


        binding.buttonCheckOut.setOnClickListener {
            findNavController().navigate(R.id.action_cartFragment_to_billingFragment)
        }
        lifecycleScope.launchWhenStarted {
            viewModel.deleteDialog.collectLatest {
                val alertDialog= AlertDialog.Builder(requireContext()).apply {
                    setTitle("Remover do Carrinho")
                    setMessage("Deseja Remover esse Item do seu carrinho?")
                    setNegativeButton("Cancelar"){dialog, _ ->
                        dialog.dismiss()

                    }
                    setPositiveButton("Sim"){dialog,_ ->
                        viewModel.deleteCartProduct(it)
                        dialog.dismiss()

                    }
                }
               alertDialog.create()
               alertDialog.show()
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.cartProducts.collectLatest {
                when(it){
                    is Resource.Loading->{
                        binding.progressbarCart.visibility= View.VISIBLE

                    }
                    is Resource.Sucess->{
                        binding.progressbarCart.visibility= View.INVISIBLE
                        if (it.data!!.isEmpty()){
                            showEmptyCart()
                            hideOtherViews()
                        }else{
                            hideEmptyCart()
                            showOtherViews()
                            cartAdapter.differ.submitList(it.data)
                        }

                    }
                    is Resource.Error->{
                        binding.progressbarCart.visibility= View.INVISIBLE
                        Toast.makeText(requireContext(),it.message, Toast.LENGTH_LONG).show()

                    }
                    else -> Unit
                }
            }
        }
    }

    private fun showOtherViews() {
        binding.apply {
            rvCart.visibility= View.VISIBLE
            totalBoxContainer.visibility= View.VISIBLE
            buttonCheckOut.visibility= View.VISIBLE
        }
    }

    private fun hideOtherViews() {
        binding.apply {
            rvCart.visibility= View.GONE
            totalBoxContainer.visibility= View.GONE
            buttonCheckOut.visibility= View.GONE
        }
    }

    private fun hideEmptyCart() {
        binding.apply {
            layoutEmptyCart.visibility= View.GONE
        }

    }

    private fun showEmptyCart() {
binding.apply {
    layoutEmptyCart.visibility= View.VISIBLE
}
    }

    private fun setupCartRV() {
        binding.rvCart.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter= cartAdapter
            addItemDecoration(VerticaltemDecoration())
        }
    }

}