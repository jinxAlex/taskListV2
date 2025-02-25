package com.example.tasklist.ui.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tasklist.R
import com.example.tasklist.databinding.LayoutTaskBinding
import com.example.tasklist.model.Task

class TasksAdapter(
    private var list: MutableList<Task>,
    private val borrarTask: (String) -> Unit,
    private val completarTask: (String) -> Unit
): RecyclerView.Adapter<TaskViewHolder>() {

    fun updateAdapter(newTasks: MutableList<Task>) {
        list = newTasks
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.layout_task,parent, false)
        return TaskViewHolder(v)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.render(list[position],borrarTask, completarTask)
    }
}

class TaskViewHolder(v: View): RecyclerView.ViewHolder(v) {

    private val binding = LayoutTaskBinding.bind(v)

    fun render(task: Task, borrarTask: (String) -> Unit, completarTask: (String) -> Unit){
        if(task.terminado){
            binding.tvTask.isChecked = true
            binding.tvTask.paintFlags  = Paint.STRIKE_THRU_TEXT_FLAG
        }else{
            binding.tvTask.isChecked = false
            binding.tvTask.paintFlags  = Paint().flags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
        binding.tvTask.text = task.nombre
        binding.btnDelete.setOnClickListener {
            borrarTask(task.nombre)
        }
        binding.tvTask.setOnCheckedChangeListener { buttonView, isChecked ->
            completarTask(task.nombre)
        }
    }

}
