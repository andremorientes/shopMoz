package com.example.shopmoz.fragments.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shopmoz.R
import com.example.shopmoz.adapters.BestProductsAdapter
import com.example.shopmoz.databinding.FragmentBaseCategoryBinding
import com.example.shopmoz.util.showBottomNavigation

open class BaseCategoryFragment: Fragment(R.layout.fragment_base_category) {
    private lateinit var binding: FragmentBaseCategoryBinding
    protected val offerAdapter: BestProductsAdapter by lazy { BestProductsAdapter() }
    protected val bestProductsAdapter: BestProductsAdapter by lazy { BestProductsAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding= FragmentBaseCategoryBinding.inflate(inflater)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOfferRev()
        setupBestProducts()



        offerAdapter.onClick ={
            val b= Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,b)
        }
        bestProductsAdapter.onClick ={
            val b= Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment,b)
        }

        binding.rvOffer.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (!recyclerView.canScrollVertically(1)&& dx !=0){
                    onOfferPagingRequest()
                }
            }
        })

        binding.nestedScrollBaseCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener{v,_, scrolly,_,_->
            if (v.getChildAt(0).bottom<= v.height + scrolly){
                onBestProductsPagingRequest()
            }

        })





    }



    fun showOfferLoading(){
        binding.offerProductsProgressBar.visibility= View.VISIBLE
    }

    fun hideOfferLoading(){
        binding.offerProductsProgressBar.visibility= View.GONE
    }

    fun showBestProductsLoading(){
        binding.bestProductsProgressBar.visibility= View.VISIBLE
    }

    fun hideBestProductsLoading(){
        binding.bestProductsProgressBar.visibility= View.GONE
    }

    open fun onOfferPagingRequest(){

    }

    open fun onBestProductsPagingRequest(){

    }



    private fun setupBestProducts() {

        binding.rvBestProducts.apply {
            layoutManager= GridLayoutManager(requireContext(),2, GridLayoutManager.VERTICAL,false)
            adapter= bestProductsAdapter
        }
    }

    private fun setupOfferRev() {

        binding.rvOffer.apply {
            layoutManager= LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter= offerAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        showBottomNavigation()
    }
}