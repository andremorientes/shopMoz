package com.example.shopmoz.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopmoz.adapters.AddressAdapter
import com.example.shopmoz.adapters.BillingProductsAdapter
import com.example.shopmoz.databinding.FragmentBillingBinding
import com.example.shopmoz.util.Resource
import com.example.shopmoz.viewmodel.BillingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class BillingFragment: Fragment() {

    private lateinit var binding: FragmentBillingBinding

    private val addressAdapter by lazy { AddressAdapter() }
    private val billingProductsAdapter by lazy { BillingProductsAdapter() }

    private val viewModel by viewModels<BillingViewModel> ()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentBillingBinding.inflate(inflater)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setupBillingProductRv()
        setupAddressRv()

        lifecycleScope.launchWhenStarted {
            viewModel.address.collectLatest {
                when(it){
                    is Resource.Loading->{
                        binding.progressbarAddress.visibility= View.VISIBLE
                    }
                    is Resource.Sucess->{
                        addressAdapter.differ.submitList(it.data)
                        binding.progressbarAddress.visibility= View.GONE
                    }
                    is Resource.Error ->{
                        binding.progressbarAddress.visibility= View.GONE
                        Toast.makeText(requireContext(),"Error ${ it.message}", Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun setupAddressRv() {
        binding.rvAddress.apply {
            layoutManager= LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL,false)
            adapter= addressAdapter
        }
    }

    private fun setupBillingProductRv() {
        binding.rvProducts.apply {
            layoutManager= LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL,false)
            adapter= billingProductsAdapter
        }
    }
}