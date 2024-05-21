package com.example.shopmoz.viewmodel

import androidx.lifecycle.ViewModel
import com.example.shopmoz.data.User
import com.example.shopmoz.util.RegisterValidation
import com.example.shopmoz.util.Resource
import com.example.shopmoz.util.validateEmail
import com.example.shopmoz.util.validatePassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private  val firebaseAuth: FirebaseAuth
): ViewModel(){

    private val _register= MutableStateFlow<Resource<FirebaseUser>>(Resource.Unspecified())
    val  register: Flow<Resource<FirebaseUser>> = _register

    fun createAccountWithEmailAndPassword(user: User, password: String){

        if (checkValidation(user, password)){

            runBlocking {
                _register.emit(Resource.Loading())
            }
            firebaseAuth.createUserWithEmailAndPassword(user.email, password)
                .addOnSuccessListener {

                    it.user?.let {
                        _register.value= Resource.Sucess(it)
                    }
                }.addOnFailureListener {

                    _register.value= Resource.Error(it.message.toString())

                }
        }else{
            val registerFieldsState = RegisterFieldsState(
                validateEmail(user.email, validatePassword(password))
            )
        }

    }

    private fun checkValidation(user:User,password: String): Boolean{
    val emailValidation= validateEmail(user.email)
    val passwordValidation= validatePassword(password)
    val shouldRegister= emailValidation is RegisterValidation.Sucess &&
            passwordValidation is RegisterValidation.Sucess

        return shouldRegister

    }

}