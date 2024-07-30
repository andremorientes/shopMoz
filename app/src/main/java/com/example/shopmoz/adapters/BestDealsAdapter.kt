package com.example.shopmoz.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.shopmoz.data.Product
import com.example.shopmoz.databinding.BestDealsRvItemBinding
import java.text.NumberFormat
import java.util.Locale

class BestDealsAdapter: RecyclerView.Adapter<BestDealsAdapter.BestDealsViewHolder>(){


    inner class  BestDealsViewHolder(private val binding: BestDealsRvItemBinding): ViewHolder(binding.root){
        fun bind(product: Product){
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(imgBestDeal)
                //Verificar se tem percentagem do valor
                product.offerPercentage?.let {
                    val remainingPricePercentage= 1f -it
                    val priceAfterOffer= remainingPricePercentage * product.price
                    val formattedPrice = NumberFormat.getNumberInstance(Locale("pt", "BR")).format(priceAfterOffer)
                    tvNovoPreco.text = "MZ $formattedPrice"
                }

                if (product.offerPercentage== null){
                    tvNovoPreco.visibility= View.GONE
                }
                val formattedPrice = NumberFormat.getNumberInstance(Locale("pt", "BR")).format(product.price)
                tvOldPrice.text= "MZ ${formattedPrice}"
                tvDealProductName.text=product.name
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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestDealsViewHolder {
        return BestDealsViewHolder(BestDealsRvItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount(): Int {

        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: BestDealsViewHolder, position: Int) {
        val product= differ.currentList[position]
        holder.bind(product)
        holder.itemView.setOnClickListener {
            onClick?.invoke(product)
        }
    }

    var onClick : ((Product)-> Unit)?= null
}