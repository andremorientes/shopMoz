package com.example.shopmoz.fragments.shopping

import android.os.Binder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shopmoz.R
import com.example.shopmoz.activitys.ShoppingActivity
import com.example.shopmoz.adapters.ColorsAdapter
import com.example.shopmoz.adapters.SizeAdapter
import com.example.shopmoz.adapters.ViewPager2Images
import com.example.shopmoz.data.CartProduct
import com.example.shopmoz.databinding.FragmentProductsDetailsBinding
import com.example.shopmoz.util.Resource
import com.example.shopmoz.util.hideBottomNavigation
import com.example.shopmoz.viewmodel.DetailsViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ProductDetailsFragment: Fragment() {
    private  val args by navArgs <ProductDetailsFragmentArgs>()
    private lateinit var  binding: FragmentProductsDetailsBinding
    private val viewPagerAdapter by lazy { ViewPager2Images() }
    private val sizesAdapter by lazy { SizeAdapter()}
    private val colorsAdapter by lazy { ColorsAdapter() }

    private var selectedColor: Int?= null
    private var selectedSize: String?=null
    private val viewModel by viewModels<DetailsViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        hideBottomNavigation()

        binding= FragmentProductsDetailsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val product = args.product

        setupSizesRv()
        setupColorRv()
        setupViewPager()

        binding.imageClose.setOnClickListener {
            findNavController().navigateUp()
        }

        sizesAdapter.onItemClick ={
            selectedSize= it
        }

        colorsAdapter.onItemClick ={
            selectedColor= it
        }

        binding.buttonAddToCart.setOnClickListener {
            viewModel.addUpdateProductInCart(CartProduct(product,1,selectedColor,selectedSize))
        }

        lifecycleScope.launchWhenStarted {
            viewModel.addToCart.collectLatest {
                when(it){
                    is Resource.Loading->{
                       binding.buttonAddToCart.startAnimation()

                    }
                    is Resource.Sucess ->{
                        binding.buttonAddToCart.revertAnimation()
                        binding.buttonAddToCart.setBackgroundColor(resources.getColor(R.color.black))
                    }

                    is Resource.Error->{
                        binding.buttonAddToCart.stopAnimation()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }
        binding.apply {
            tvProductName.text= product.name
            tvProductPrice.text="MZN ${product.price}"
            tvProducDescription.text= product.description



        }

        viewPagerAdapter.differ.submitList(product.images)
        product.colors?.let {  colorsAdapter.differ.submitList(it)}
        product.sizes?.let { sizesAdapter.differ.submitList(it) }
    }

    private fun setupViewPager() {
        binding.apply {
            viewPagerProductImage.adapter= viewPagerAdapter
        }
    }

    private fun setupColorRv() {
        binding.rvColors.apply {
            adapter= colorsAdapter
            layoutManager= LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setupSizesRv() {
        binding.rvSizes.apply {
            adapter= sizesAdapter
            layoutManager= LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false)
        }
    }

}