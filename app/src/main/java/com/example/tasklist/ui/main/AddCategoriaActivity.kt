package com.example.tasklist.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tasklist.databinding.ActivityCategoryAddBinding
import com.example.tasklist.ui.adapter.CategoryAdapter

class AddCategoriaActivity : AppCompatActivity() {

    private val viewModel: ListCategoriaViewModel by viewModels()
    private lateinit var binding: ActivityCategoryAddBinding
    private lateinit var adapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setRecycler()
        setListeners()
        observeViewModel()
    }

    private fun setRecycler() {
        binding.recycler.layoutManager = LinearLayoutManager(this)
        adapter = CategoryAdapter(emptyList()) { categoria ->
            viewModel.removeCategory(categoria)
        }
        binding.recycler.adapter = adapter
    }

    private fun setListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }
        binding.tfEmail.setEndIconOnClickListener {
            addCategoria()
        }
    }

    private fun addCategoria() {
        val categoryName = binding.etCategory.text.toString().trim()
        if (categoryName.isNotEmpty()) {
            viewModel.addCategory(categoryName)
            binding.etCategory.text?.clear()
        }
    }

    private fun observeViewModel() {
        viewModel.listCategorias.observe(this) { categorias ->
            adapter.updateCategories(categorias)
        }
    }
}