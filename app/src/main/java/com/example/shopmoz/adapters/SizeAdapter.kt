package com.example.shopmoz.adapters

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.shopmoz.databinding.ColorRvItemBinding
import com.example.shopmoz.databinding.SizeRvItemBinding

class SizeAdapter:RecyclerView.Adapter<SizeAdapter.SizesViewHolder>() {

    private var selectedPosition= -1
    inner class  SizesViewHolder(private val binding: SizeRvItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(size: String, position: Int){
            binding.tvSize.text= size
            if (position== selectedPosition){// size selected
                binding.apply {
                    imageShadow.visibility= View.VISIBLE
                }
            }else{// size not selected
                binding.apply {
                    imageShadow.visibility= View.INVISIBLE

                }
            }

        }
    }

    private val diffCallBack= object : DiffUtil.ItemCallback<String>(){
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return  oldItem== newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem== newItem
        }

    }

    val differ = AsyncListDiffer(this, diffCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizesViewHolder {
        return SizesViewHolder(
            SizeRvItemBinding.inflate(LayoutInflater.from(parent.context))
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: SizesViewHolder, position: Int) {
        val size = differ.currentList[position]
        holder.bind(size,position)

        holder.itemView.setOnClickListener {
            if (selectedPosition>=0)
                notifyItemChanged(selectedPosition)
            selectedPosition= holder.adapterPosition
            notifyItemChanged(selectedPosition)
            onItemClick?.invoke(size)
        }
    }

    var onItemClick:((String)->Unit)?=null
}