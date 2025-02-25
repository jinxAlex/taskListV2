package com.example.tasklist.ui.main

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tasklist.R
import com.example.tasklist.data.UserData
import com.example.tasklist.databinding.ActivityAddBinding
import com.example.tasklist.model.Task
import com.example.tasklist.utils.encodeEmail
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBinding

    private var categorias: MutableList<String> = mutableListOf()

    //variables para la tarea
    private var name = ""

    private var tiempo = 0

    private var altaPrioridad = false

    //variable Firebase
    private val firebase: DatabaseReference = FirebaseDatabase.getInstance().getReference("taskList").child(UserData.getEmail().encodeEmail()).child("tasks")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setListeners()
        recogerDatos()
        ponerDatos()
    }

    private fun ponerDatos() {
        binding.spCategoria.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item,categorias)
    }

    private fun recogerDatos() {
        val datos = intent.extras

        categorias = datos?.getStringArrayList("CATEGORIAS") ?: arrayListOf()
    }

    private fun comprobarDatos(): Boolean {
        var esCorrecto = true
        name = binding.etTask.text.toString()
        tiempo = binding.sbTime.value.toInt()
        altaPrioridad = binding.cbPrioridad.isChecked

        if(name.isEmpty()){
            esCorrecto = false
            binding.tfTaskName.error = "ERROR: Debes de agregar un nombre"
        }
        if(tiempo == 0){
            esCorrecto = false
            Toast.makeText(this,"ERROR: Se debe de añadir un determinado tiempo estimado",Toast.LENGTH_SHORT).show()
        }
        return esCorrecto
    }

    private fun enviarDatos() {
        val task = Task(
            name,
            tiempo,
            false,
            binding.spCategoria.getItemAtPosition(binding.spCategoria.selectedItemPosition).toString(),
            altaPrioridad
            )
        firebase.push().setValue(task)
            .addOnSuccessListener {
                Toast.makeText(this,"Se ha agregado la tarea con éxito",Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this,"No se ha agregado la tarea con éxito",Toast.LENGTH_SHORT).show()
            }
    }

    private fun setListeners() {
        binding.btnEnviar.setOnClickListener {
            if(comprobarDatos()){
                enviarDatos()
            }
        }
    }


}