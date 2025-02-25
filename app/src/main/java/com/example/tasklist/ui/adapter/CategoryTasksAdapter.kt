package com.example.tasklist.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tasklist.R
import com.example.tasklist.databinding.LayoutCategoryTasksBinding

class CategoryTasksAdapter(
    private var list: List<String>,
    private val changeTaskList: (MutableList<String>) -> Unit
): RecyclerView.Adapter<CategoryTasksViewHolder>(){

    private val chipsSelected: MutableList<String> = mutableListOf()

    fun updateCategories(newCategories: List<String>) {
        list = newCategories
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryTasksViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_category_tasks,parent,false)
        return CategoryTasksViewHolder(v)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: CategoryTasksViewHolder, position: Int) {
        holder.render(list[position],chipsSelected, changeTaskList)
    }

}

class CategoryTasksViewHolder(v: View): RecyclerView.ViewHolder(v) {

    val binding = LayoutCategoryTasksBinding.bind(v)

    fun render(category: String, chipsSelected: MutableList<String>, changeTaskList: (MutableList<String>) -> Unit){
        binding.chip.text = category
        binding.chip.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                chipsSelected.add(category)
            }else{
                chipsSelected.remove(category)
                Log.d("ISCHECKED","LO ESTA HACIENDO")
            }
            changeTaskList(chipsSelected)
        }
    }
}
