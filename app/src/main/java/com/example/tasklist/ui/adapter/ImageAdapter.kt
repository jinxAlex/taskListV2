package com.example.tasklist.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tasklist.R
import com.example.tasklist.databinding.LayoutImageBinding
import com.squareup.picasso.Picasso

class ImageAdapter(
    private var list: List<String>,
    private var previewImage: (String) -> Unit
) : RecyclerView.Adapter<ImageViewHolder>(){

    fun updateImages(newCategories: List<String>) {
        list = newCategories
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_image,parent,false)
        return ImageViewHolder(v)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.render(list[position],previewImage)
    }

}

class ImageViewHolder(v: View): RecyclerView.ViewHolder(v) {
    private val binding = LayoutImageBinding.bind(v)

    fun render(image: String, previewImage: (String) -> Unit){
        Picasso.get().load(image).into(binding.ivImagen)
        binding.ivImagen.setOnClickListener {
            previewImage(image)
        }
    }
}
