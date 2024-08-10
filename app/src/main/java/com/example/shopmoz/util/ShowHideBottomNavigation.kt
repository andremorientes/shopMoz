package com.example.shopmoz.util

import android.view.View
import androidx.fragment.app.Fragment
import com.example.shopmoz.R
import com.example.shopmoz.activitys.ShoppingActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

fun Fragment.hideBottomNavigation(){
    val bottomNavigationView= (activity as ShoppingActivity).findViewById<BottomNavigationView>(
        R.id.bottomNavigation)

    bottomNavigationView.visibility= View.GONE
}

fun Fragment.showBottomNavigation(){
    val bottomNavigationView= (activity as ShoppingActivity).findViewById<BottomNavigationView>(
        R.id.bottomNavigation)

    bottomNavigationView.visibility= View.VISIBLE
}