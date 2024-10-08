package com.example.shopmoz.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.shopmoz.data.Product
import com.example.shopmoz.databinding.SpecialRvItemBinding
import java.text.NumberFormat
import java.util.Locale

class SpecialProductsAdapter: RecyclerView.Adapter<SpecialProductsAdapter.SpecialProductsViewHolder>() {

    inner class SpecialProductsViewHolder(private val binding: SpecialRvItemBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(product: Product){
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(imageSpecialRvItem)
                tvSpecialProductName.text=product.name
                val formattedPrice = NumberFormat.getNumberInstance(Locale("pt", "BR")).format(product.price)
                tvSpecialProdutPrice.text= "MZ ${formattedPrice}"


            }
        }

    }

   private  val diffCallback= object : DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id== newItem.id

        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem==newItem
        }

    }

    val differ = AsyncListDiffer(this, diffCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecialProductsViewHolder {
        return SpecialProductsViewHolder(SpecialRvItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return  differ.currentList.size
    }

    override fun onBindViewHolder(holder: SpecialProductsViewHolder, position: Int) {

        if (differ.currentList.isNotEmpty()){
            val product= differ.currentList[position]
            holder.bind(product)

            holder.itemView.setOnClickListener {
                onClick?.invoke(product)
            }

        }

    }

    var onClick : ((Product)-> Unit)?= null

}