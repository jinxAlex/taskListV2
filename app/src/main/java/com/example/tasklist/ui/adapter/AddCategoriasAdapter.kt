package com.example.tasklist.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tasklist.R
import com.example.tasklist.databinding.LayoutCategoryAddBinding

class CategoryAdapter(
    private var list: List<String>,
    private val deleteCategoria: (String) -> Unit
): RecyclerView.Adapter<CategoryAddViewHolder>() {

    fun updateCategories(newCategories: List<String>) {
        list = newCategories
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryAddViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_category_add,parent, false)
        return CategoryAddViewHolder(v)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: CategoryAddViewHolder, position: Int) {
        holder.render(list[position],deleteCategoria)
    }

}

class CategoryAddViewHolder(v: View): RecyclerView.ViewHolder(v) {

    private val binding = LayoutCategoryAddBinding.bind(v)

    fun render(category: String, deleteCategory: (String) -> Unit) {
        binding.tvCategory.text = category
        binding.imageButton.setOnClickListener{
            deleteCategory(category)
        }
    }

}
