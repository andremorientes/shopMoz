package com.example.shopmoz.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shopmoz.data.Product
import com.example.shopmoz.databinding.ProductRvItemBinding
import com.example.shopmoz.helper.getProductPrice
import java.text.NumberFormat
import java.util.Locale

class BestProductsAdapter: RecyclerView.Adapter<BestProductsAdapter.BestProductsViewHolder>() {

    inner class  BestProductsViewHolder(private val binding: ProductRvItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(product: Product){
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(imgProduct)
                //Verificar se tem percentagem do valor

                if(product.offerPercentage != null){
                    val priceAfterOffer= product.offerPercentage.getProductPrice(product.price)

                    tvNewPrice.text = "MZn ${String.format("%.2f", priceAfterOffer) }"
                    tvPrice.paintFlags= Paint.STRIKE_THRU_TEXT_FLAG

                }

                if (product.offerPercentage== null){
                    tvNewPrice.visibility= View.GONE
                }
                val formattedPrice = NumberFormat.getNumberInstance(Locale("pt", "BR")).format(product.price)
                tvPrice.text= "MZ ${formattedPrice}"

                tvName.text=product.name
            }

        }
    }

    private val diffCallback= object : DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id== newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem==newItem
        }

    }

    val differ= AsyncListDiffer(this,diffCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestProductsViewHolder {
        return BestProductsViewHolder(ProductRvItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount(): Int {

        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: BestProductsViewHolder, position: Int) {
        val product= differ.currentList[position]
        holder.bind(product)

        holder.itemView.setOnClickListener {
            onClick?.invoke(product)
        }
    }

    var onClick : ((Product)-> Unit)?= null
}