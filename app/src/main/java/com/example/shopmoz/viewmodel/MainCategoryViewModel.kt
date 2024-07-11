package com.example.shopmoz.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shopmoz.data.Product
import com.example.shopmoz.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainCategoryViewModel  @Inject constructor(
    private  val firestore: FirebaseFirestore
) : ViewModel() {

    private val _specialProducts= MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val specialProducts : StateFlow<Resource<List<Product>>> = _specialProducts
    init {
      fetchSpecialProduct()
    }

    fun fetchSpecialProduct(){
        viewModelScope.launch {
            _specialProducts.emit(Resource.Loading())
        }

        //Buscando a Categoria ESPECIAL PRODUTO no Firestore
        firestore.collection("Products")
            .whereEqualTo("category", "Especial Produto").get().
        addOnSuccessListener { result->
            val specialProdutList= result.toObjects(Product::class.java)
            viewModelScope.launch {
                _specialProducts.emit(Resource.Sucess(specialProdutList))
            }

        }.addOnFailureListener {
            viewModelScope.launch {
                _specialProducts.emit(Resource.Error(it.message.toString()))
            }

        }
    }
}