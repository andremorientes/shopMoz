package com.example.shopmoz.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopmoz.data.CartProduct
import com.example.shopmoz.firebase.FirebaseCommon
import com.example.shopmoz.helper.getProductPrice
import com.example.shopmoz.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
): ViewModel() {

    private val _cartProducts= MutableStateFlow<Resource<List<CartProduct>>>(Resource.Unspecified())
    val cartProducts= _cartProducts.asStateFlow()

    val productsPrice= cartProducts.map { 
        when(it){
            is Resource.Sucess ->{
                calculatePrice(it.data!!)
                
            }
            else -> null
        }
    }

    private val _deleteDialog = MutableSharedFlow<CartProduct>()
    val deleteDialog = _deleteDialog.asSharedFlow()

    private var cartProductDocuments= emptyList<DocumentSnapshot>()
    fun deleteCartProduct(cartProduct: CartProduct) {

        val index= cartProducts.value.data?.indexOf(cartProduct)
        if(index != null && index != -1){
            val documentsId= cartProductDocuments[index].id
            firestore.collection("user").document(auth.uid!!).collection("cart")
                .document(documentsId).delete()
        }


    }



    private fun calculatePrice(data: List<CartProduct>): Float {
        return data.sumByDouble { cartProduct ->
            (cartProduct.product.offerPercentage.getProductPrice(cartProduct.product.price)* cartProduct.quantity).toDouble()
        }.toFloat()

    }


    init {
        getCartProducts()
    }

    private fun getCartProducts(){
        viewModelScope.launch { _cartProducts.emit(Resource.Loading()) }
        firestore.collection("user").document(auth.uid!!).collection("cart")
            .addSnapshotListener { value, error ->

                if (error != null || value== null){
                    viewModelScope.launch { _cartProducts.emit(Resource.Error(error?.message.toString())) }
                }else{
                    cartProductDocuments= value.documents
                    val cartProducts = value.toObjects(CartProduct::class.java)
                    viewModelScope.launch { _cartProducts.emit(Resource.Sucess(cartProducts)) }
                }

            }
    }

    fun changeQuantity(
        cartProduct: CartProduct,
        quantityChanging: FirebaseCommon.QuantityChanging
    ){


        val index = cartProducts.value.data?.indexOf(cartProduct)

        /**
         * index could be equal to -1 if the function [getCartProducts] delays wich will also delay the result we expect to be inside the [_cartProducts]
         * and to prevent the app from crashing we make a check
         * */
        if (index != null && index != -1){
            val documentsId= cartProductDocuments[index].id
            when(quantityChanging){
                FirebaseCommon.QuantityChanging.INCREASE ->{
                    viewModelScope.launch { _cartProducts.emit(Resource.Loading()) }
                    increaseQuantity(documentsId)
                }
                FirebaseCommon.QuantityChanging.DESCREASE ->{

                    if (cartProduct.quantity== 1){
                        viewModelScope.launch { _deleteDialog.emit(CartProduct()) }
                        return
                    }
                    viewModelScope.launch { _cartProducts.emit(Resource.Loading()) }
                    decreaseQuantity(documentsId)
                }
            }
        }


    }

    private fun decreaseQuantity(documentsId: String) {
        firebaseCommon.decreaseQuantity(documentsId){result, exception ->
            if (exception != null)
                viewModelScope.launch {_cartProducts.emit(Resource.Error(exception.message.toString()))  }

        }

    }

    private fun increaseQuantity(documentsId: String) {
        firebaseCommon.increaseQuantity(documentsId){result, exception ->
            if (exception != null)
                viewModelScope.launch {_cartProducts.emit(Resource.Error(exception.message.toString()))  }

        }
    }
}