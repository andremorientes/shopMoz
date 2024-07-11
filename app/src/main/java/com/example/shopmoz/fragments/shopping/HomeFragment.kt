package com.example.shopmoz.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.shopmoz.R
import com.example.shopmoz.databinding.FragmentHomeBinding
import com.example.shopmoz.fragments.categories.AccessoryFragment
import com.example.shopmoz.fragments.categories.ChairFragment
import com.example.shopmoz.fragments.categories.CupBoardFragment
import com.example.shopmoz.fragments.categories.FurnitureFragment
import com.example.shopmoz.fragments.categories.MainCategoryFragment
import com.example.shopmoz.fragments.categories.TableFragment

class HomeFragment: Fragment(R.layout.fragment_home) {

    private lateinit var binding : FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoriesFragments= arrayListOf<Fragment>(
            MainCategoryFragment(),
            ChairFragment(),
            CupBoardFragment(),
            TableFragment(),
            AccessoryFragment(),
            FurnitureFragment()
        )
    }
}