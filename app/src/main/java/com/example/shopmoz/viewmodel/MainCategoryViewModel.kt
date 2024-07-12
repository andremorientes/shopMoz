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

    private val _bestDealsProducts= MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestDealsProducts : StateFlow<Resource<List<Product>>> = _bestDealsProducts

    private val _bestProducts= MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val bestProducts : StateFlow<Resource<List<Product>>> = _bestProducts
    init {
      fetchSpecialProduct()
        fetchBestDealsProducts()
        fetchBestProducts()
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

    fun fetchBestDealsProducts(){
        viewModelScope.launch {
            _bestDealsProducts
                .emit(Resource.Loading())
        }

        //Buscando a Categoria Best Deals no Firestore
        firestore.collection("Products")
            .whereEqualTo("category", "Best deals").get().
            addOnSuccessListener { result->
                val bestDealsProdutList= result.toObjects(Product::class.java)
                viewModelScope.launch {
                    _bestDealsProducts.emit(Resource.Sucess(bestDealsProdutList))
                }

            }.addOnFailureListener {
                viewModelScope.launch {
                    _bestDealsProducts.emit(Resource.Error(it.message.toString()))
                }

            }
    }

    fun fetchBestProducts(){
        viewModelScope.launch {
            _bestProducts
                .emit(Resource.Loading())
        }

        //Buscando a Categoria Best Deals no Firestore
        firestore.collection("Products").get().
            addOnSuccessListener { result->
                val bestProdutList= result.toObjects(Product::class.java)
                viewModelScope.launch {
                    _bestProducts.emit(Resource.Sucess(bestProdutList))
                }

            }.addOnFailureListener {
                viewModelScope.launch {
                    _bestProducts.emit(Resource.Error(it.message.toString()))
                }

            }
    }
}