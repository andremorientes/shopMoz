package com.example.shopmoz.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.shopmoz.data.Address
import com.example.shopmoz.databinding.FragmentAdressBinding
import com.example.shopmoz.util.Resource
import com.example.shopmoz.viewmodel.AddressViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint

class AddressFragment: Fragment() {
    private lateinit var binding: FragmentAdressBinding

    val viewModel by viewModels<AddressViewModel> ()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.addNewAddress.collectLatest {
                when(it){

                    is Resource.Loading ->{
                        binding.progressbarAddress.visibility= View.VISIBLE
                    }
                    is Resource.Sucess ->{
                        binding.progressbarAddress.visibility= View.INVISIBLE
                        findNavController().navigateUp()
                    }
                    is Resource.Error->{
                        Toast.makeText(requireContext(),it.message.toString(), Toast.LENGTH_LONG).show()
                    }
                    else ->Unit
                    }
                }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.error.collectLatest {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()

            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentAdressBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            buttonSave.setOnClickListener {
                val addressTitle= edAddressTitle.text.toString()
                val fullname= edFullName.text.toString()
                val street= edStreet.text.toString()
                val city= edCity.text.toString()
                val phone= edPhone.text.toString()
                val state= edState.text.toString()

                val address= Address(addressTitle,fullname,street, phone, city, state)
                viewModel.addAdress(address)
            }
        }
    }

}