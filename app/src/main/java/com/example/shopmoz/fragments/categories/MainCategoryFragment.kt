package com.example.shopmoz.fragments.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopmoz.R
import com.example.shopmoz.adapters.BestDealsAdapter
import com.example.shopmoz.adapters.BestProductsAdapter
import com.example.shopmoz.adapters.SpecialProductsAdapter
import com.example.shopmoz.databinding.FragmentMainCategoryBinding
import com.example.shopmoz.util.Resource
import com.example.shopmoz.viewmodel.MainCategoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

private  val TAG="MainCategoryFragment"
@AndroidEntryPoint
class MainCategoryFragment: Fragment(R.layout.fragment_main_category) {
    private lateinit var binding: FragmentMainCategoryBinding
    private lateinit var specialProductsAdapter: SpecialProductsAdapter
    private lateinit var bestDealsAdapter: BestDealsAdapter
    private lateinit var bestProductsAdapter: BestProductsAdapter
    private val viewModel by viewModels<MainCategoryViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentMainCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpecialProductsRv()

        setupBestDealsRv()

        setupBestProductsRv()

        specialProductsAdapter.onClick ={
            val b= Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,b)
        }
        bestDealsAdapter.onClick ={
            val b= Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,b)
        }
        bestProductsAdapter.onClick ={
            val b= Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,b)
        }


        lifecycleScope.launchWhenStarted {
            viewModel.specialProducts.collectLatest {
                when(it){
                    is Resource.Loading->{
                        showLoading()
                    }
                    is Resource.Sucess->{
                        specialProductsAdapter.differ.submitList(it.data)
                        hideLoading()
                    }
                    is Resource.Error->{
                        hideLoading()
                        Log.e(TAG,it.message.toString())
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                    else-> Unit

                }
            }

        }

        lifecycleScope.launchWhenStarted {
            viewModel.bestDealsProducts.collectLatest {
                when(it){
                    is Resource.Loading->{
                        showLoading()
                    }
                    is Resource.Sucess->{
                        bestDealsAdapter.differ.submitList(it.data)
                        hideLoading()
                    }
                    is Resource.Error->{
                        hideLoading()
                        Log.e(TAG,it.message.toString())
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                    else-> Unit

                }
            }

        }
        lifecycleScope.launchWhenStarted {
            viewModel.bestProducts.collectLatest {
                when(it){
                    is Resource.Loading->{
                        binding.bestProductProgressBar.visibility= View.VISIBLE
                    }
                    is Resource.Sucess->{
                        bestProductsAdapter.differ.submitList(it.data)
                        binding.bestProductProgressBar.visibility= View.GONE

                    }
                    is Resource.Error->{
                        binding.bestProductProgressBar.visibility= View.GONE
                        Log.e(TAG,it.message.toString())
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                    else-> Unit

                }
            }

        }

        binding.nestedScrollMainCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener{v,_,scrolly,_,_->

            if (v.getChildAt(0).bottom<= v.height * scrolly){
                viewModel.fetchBestProducts()
            }
        })

    }



    private fun setupBestProductsRv() {
        bestProductsAdapter= BestProductsAdapter()
        binding.rvBestProducts.apply {
            layoutManager= GridLayoutManager(requireContext(),2, GridLayoutManager.VERTICAL,false)
            adapter= bestProductsAdapter
        }
    }

    private fun setupBestDealsRv() {
        bestDealsAdapter= BestDealsAdapter()
        binding.rvBestDealProducts.apply {
            layoutManager= LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false)
            adapter= bestDealsAdapter

        }
    }

    private fun hideLoading() {
        binding.mainCategoryProgressBar.visibility=View.GONE
    }

    private fun showLoading() {
        binding.mainCategoryProgressBar.visibility= View.VISIBLE
    }

    private fun setupSpecialProductsRv() {
       specialProductsAdapter= SpecialProductsAdapter()
        binding.rvSpecialProduct.apply {
            layoutManager= LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false)
            adapter= specialProductsAdapter

        }
    }
}