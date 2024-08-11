package com.example.shopmoz.firebase

import com.example.shopmoz.data.CartProduct
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseCommon(
    private val firestore : FirebaseFirestore,
    private val auth: FirebaseAuth
) {

    private val cartCollection= firestore.collection("user").document(auth.uid!!).collection("cart")

    fun addProductToCart(cartProduct: CartProduct, onResult: (CartProduct?, Exception?) -> Unit) {
        val query = cartCollection.whereEqualTo("productId", cartProduct.product.id)
        query.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val documents = task.result.documents
                if (documents.isNotEmpty()) {
                    // Product already exists in cart, update quantity
                    val documentId = documents[0].id
                    increaseQuantity(documentId, { _, exception ->
                        if (exception == null) {
                            onResult(cartProduct, null)
                        } else {
                            onResult(null, exception)
                        }
                    })
                } else {
                    // Product doesn't exist in cart, add new document
                    cartCollection.document().set(cartProduct).addOnSuccessListener {
                        onResult(cartProduct, null)
                    }.addOnFailureListener {
                        onResult(null, it)
                    }
                }
            } else {
                onResult(null, task.exception)
            }
        }
    }

    fun increaseQuantity(documentId:String, onResult: (String?, Exception?) -> Unit){
        firestore.runTransaction {transition ->
            val documentRef= cartCollection.document(documentId)
            val document = transition.get(documentRef)
            val productObject= document.toObject(CartProduct::class.java)
            productObject?.let { cartProduct ->
                val newQuantity = cartProduct.quantity + 1
                val newProductObject= cartProduct.copy(quantity = newQuantity)
                transition.set(documentRef, newProductObject)
            }

        }.addOnSuccessListener {
            onResult(documentId, null)
        }.addOnFailureListener {
            onResult(null, it)
        }
    }

    fun decreaseQuantity(documentId:String, onResult: (String?, Exception?) -> Unit){
        firestore.runTransaction {transition ->
            val documentRef= cartCollection.document(documentId)
            val document = transition.get(documentRef)
            val productObject= document.toObject(CartProduct::class.java)
            productObject?.let { cartProduct ->
                val newQuantity = cartProduct.quantity -1
                val newProductObject= cartProduct.copy(quantity = newQuantity)
                transition.set(documentRef, newProductObject)
            }

        }.addOnSuccessListener {
            onResult(documentId, null)
        }.addOnFailureListener {
            onResult(null, it)
        }
    }

    enum class  QuantityChanging{
        INCREASE, DESCREASE
    }
}