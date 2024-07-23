package com.example.shopmoz.data

sealed class Category(val category: String) {

    object  Chair: Category("Chair")
    object  Cupboard: Category("Cupboard")
    object  Table: Category("Table")
    object  Acessory: Category("Acessory")
    object  Furtunire: Category("Furtunire")
}