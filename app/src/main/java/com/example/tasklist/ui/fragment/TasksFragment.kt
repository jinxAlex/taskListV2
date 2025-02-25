package com.example.tasklist.ui.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tasklist.R
import com.example.tasklist.data.UserData
import com.example.tasklist.model.Task
import com.example.tasklist.ui.adapter.CategoryTasksAdapter
import com.example.tasklist.ui.adapter.TasksAdapter
import com.example.tasklist.ui.main.ListCategoriaViewModel
import com.example.tasklist.utils.encodeEmail
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TasksFragment : Fragment() {

    private val viewModel: ListCategoriaViewModel by viewModels()

    // Views
    private lateinit var tvUsername: TextView

    private lateinit var imagen: de.hdodenhof.circleimageview.CircleImageView

    private lateinit var recyclerCategorias: RecyclerView

    private lateinit var recyclerTasks: RecyclerView

    private lateinit var adapterCategorias: CategoryTasksAdapter

    private lateinit var adapterTasks: TasksAdapter

    // Variables Firebase
    private lateinit var tasksDatabaseReference: DatabaseReference

    private var listTask: MutableList<Task> = mutableListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViews(view)
        setRecyclers()
        setAllUserData() //Esta función va a poner todos los datos del usuario, el nombre, que se muestren solo las categorías,...
    }

    private fun setAllUserData() {
        lifecycleScope.launch {
            delay(1000)
            viewModel.getListCategories()
            tvUsername.text = UserData.getName()
            if (!UserData.getImage().isNullOrBlank()) {
                Picasso.get().load(UserData.getImage()).into(imagen)
            } else {
                Log.e("TasksFragment", "URL de imagen vacía")
            }
            viewModel.listCategorias.observe(viewLifecycleOwner) { categories ->
                rellenarListaTareas(categories.toMutableList())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setAllUserData()
    }

    private fun setRecyclers() {
        recyclerCategorias.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false) //No puedo pasarle this en un fragment, entonces uso requireContext()

        adapterCategorias = CategoryTasksAdapter(emptyList(),{rellenarListaTareas(it)})

        viewModel.listCategorias.observe(viewLifecycleOwner) { categories ->
            adapterCategorias.updateCategories(categories)
        }

        recyclerCategorias.adapter = adapterCategorias

        recyclerTasks.layoutManager = LinearLayoutManager(requireContext())

        adapterTasks = TasksAdapter(mutableListOf(),{ borrarTask(it) }) { updateTask(it) }

        recyclerTasks.adapter = adapterTasks
    }

    private fun updateTask(taskNombre: String) {
        tasksDatabaseReference.get()
            .addOnSuccessListener {
                for (nodo in it.children){
                    val taskNombreEncontrada = nodo.child("nombre").getValue()
                    if(taskNombre == taskNombreEncontrada){
                        val taskReferencia = nodo.ref
                        val estado = nodo.child("terminado").getValue(Boolean::class.java) ?: false
                        if(estado){
                            taskReferencia.child("terminado").setValue(false)
                        }else{
                            taskReferencia.child("terminado").setValue(true)
                        }
                    }
                }
            }
    }

    private fun borrarTask(nombre: String) {
        tasksDatabaseReference.get()
            .addOnSuccessListener {
                for(nodo in it.children){
                    val taskNombre = nodo.child("nombre").getValue()
                    if(taskNombre == nombre){
                        nodo.ref.removeValue()
                    }
                }
            }
    }

    private fun setViews(view: View) {
        tasksDatabaseReference = FirebaseDatabase.getInstance().getReference("taskList").child(UserData.getEmail().encodeEmail()).child("tasks")
        recyclerTasks = view.findViewById(R.id.recyclerTasks)
        recyclerCategorias = view.findViewById(R.id.recycler_categories)
        tvUsername = view.findViewById(R.id.tv_username)
        imagen = view.findViewById(R.id.perfil_imagen)
    }

    private fun rellenarListaTareas(categoriesSelected: MutableList<String>) {
        tasksDatabaseReference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                listTask.clear()
                for(nodo in snapshot.children){
                    val task =nodo.getValue(Task::class.java)
                    if(task!=null && task.categoria in categoriesSelected){
                        listTask.add(task)
                    }
                }
                listTask.sortByDescending { it.altaPrioridad }
                adapterTasks.updateAdapter(listTask)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tasks, container, false)
    }
}