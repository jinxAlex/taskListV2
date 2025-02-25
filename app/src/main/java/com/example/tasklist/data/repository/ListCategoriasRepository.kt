package com.example.tasklist.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.tasklist.data.UserData
import com.example.tasklist.data.db.Crud
import com.example.tasklist.model.User
import com.example.tasklist.utils.encodeEmail
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.getValue

class ListCategoriasRepository {

    // Referencia base de datos
    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("taskList")

    private val _listCategorias = MutableLiveData<List<String>>()

    val listCategorias: LiveData<List<String>> = _listCategorias

    fun getListCategories() {
        _listCategorias.value = Crud().readCategorias()
    }

    fun addCategoria(categoria: String) {
        Crud().create(categoria)
    }

    fun removeCategorias(categoria: String) {
        Crud().deleteCategoria(categoria)
        removeTasks(categoria)
    }

    private fun removeTasks(categoria: String) {
        val nodoUsuario = UserData.getEmail().encodeEmail()
        database.child(nodoUsuario).child("tasks").get()
            .addOnSuccessListener {
                for(nodo in it.children){
                    val categoriaTask = nodo.child("categoria").getValue(String::class.java)
                    if(categoriaTask == categoria){
                        nodo.ref.removeValue()
                    }
                }
            }
    }
}