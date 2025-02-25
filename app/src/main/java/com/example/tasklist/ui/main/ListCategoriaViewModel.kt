package com.example.tasklist.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.tasklist.data.repository.ListCategoriasRepository

class ListCategoriaViewModel: ViewModel() {
    private val repository = ListCategoriasRepository()

    private val _listCategorias = repository.listCategorias

    val listCategorias: LiveData<List<String>> = _listCategorias

    init {
        getListCategories()
    }

    fun getListCategories(){
        repository.getListCategories()
    }

    fun addCategory(categoria: String){
        repository.addCategoria(categoria)
        getListCategories()
    }

    fun removeCategory(categoria: String){
        repository.removeCategorias(categoria)
        getListCategories()
    }

}