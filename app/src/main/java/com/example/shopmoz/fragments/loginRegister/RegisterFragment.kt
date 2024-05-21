package com.example.shopmoz.fragments.loginRegister

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.shopmoz.R
import com.example.shopmoz.data.User
import com.example.shopmoz.databinding.FragmentRegisterBinding
import com.example.shopmoz.util.Resource
import com.example.shopmoz.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

val TAG= "RegisterFragament"

@AndroidEntryPoint
class RegisterFragment: Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModels<RegisterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            buttonRegister.setOnClickListener {
                val  user = User(
                    edFirstNameRegister.text.toString().trim(),
                    edLastNameRegister.text.toString().trim(),
                    edEmailRegister.text.toString().trim()
                )
                val  password= edPasswordRegister.text.toString()
                viewModel.createAccountWithEmailAndPassword(user, password)

            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.register.collect{
                when(it){
                    is Resource.Loading ->{
                        binding.buttonRegister.startAnimation()
                        Log.i("de", it.message.toString())

                    }
                    is Resource.Sucess ->{
                        Log.d("test", it.message.toString())
                        binding.buttonRegister.revertAnimation()

                    }
                    is Resource.Error ->{
                        Log.e(TAG, it.message.toString())
                        binding.buttonRegister.revertAnimation()
                    }
                    else -> Unit
                }
            }
        }
    }


}