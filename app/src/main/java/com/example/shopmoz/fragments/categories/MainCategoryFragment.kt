package com.example.shopmoz.fragments.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopmoz.R
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