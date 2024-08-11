package com.example.shopmoz.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shopmoz.data.CartProduct
import com.example.shopmoz.data.Product
import com.example.shopmoz.databinding.CartProductItemBinding
import com.example.shopmoz.helper.getProductPrice

class CartProductsAdapter  : RecyclerView.Adapter<CartProductsAdapter.CartProductsViewHolder>() {

    inner class CartProductsViewHolder( val binding: CartProductItemBinding):
        RecyclerView.ViewHolder(binding.root){
        fun bind(cartproduct: CartProduct){
            binding.apply {
                Glide.with(itemView).load(cartproduct.product.images[0]).into(imageCartProduct)
                tvProductCartName.text=cartproduct.product.name
                tvCartProductQuantity.text= cartproduct.quantity.toString()

                val priceAfterPercentage = cartproduct.product.offerPercentage.getProductPrice(cartproduct.product.price)
                tvProductCartPrice.text= "MZn ${String.format("%.2f", priceAfterPercentage) }"

                imageCartProductColor.setImageDrawable(ColorDrawable(cartproduct.selectedColor?: Color.TRANSPARENT))
                tvCartProductSize.text= cartproduct.selectedSize?:"".also { imageCartProductSize.setImageDrawable(ColorDrawable(Color.TRANSPARENT)) }

            }
        }

    }

    private  val diffCallback= object : DiffUtil.ItemCallback<CartProduct>(){
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem.product.id== newItem.product.id

        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem==newItem
        }

    }

    val differ = AsyncListDiffer(this, diffCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartProductsViewHolder {
        return CartProductsViewHolder(CartProductItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return  differ.currentList.size
    }

    override fun onBindViewHolder(holder: CartProductsViewHolder, position: Int) {

        if (differ.currentList.isNotEmpty()){
            val cartProduct= differ.currentList[position]
            holder.bind(cartProduct)

            holder.itemView.setOnClickListener {
                onProductClick?.invoke(cartProduct)
            }

            holder.binding.imagePlus.setOnClickListener {
                onPlustClick?.invoke(cartProduct)
            }

            holder.binding.imageMinus.setOnClickListener {
                onMinusClick?.invoke(cartProduct)
            }

        }

    }

    var onProductClick : ((CartProduct)-> Unit)?= null
    var onPlustClick : ((CartProduct)-> Unit)?= null
    var onMinusClick : ((CartProduct)-> Unit)?= null

}