package com.example.tasklist.ui.main

import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.tasklist.R
import com.example.tasklist.data.UserData
import com.example.tasklist.data.providers.ApiImage
import com.example.tasklist.databinding.ActivityChangeImageBinding
import com.example.tasklist.ui.adapter.ImageAdapter
import com.example.tasklist.utils.encodeEmail
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CambiarImagenActivity : AppCompatActivity() {

    private var database: DatabaseReference = FirebaseDatabase.getInstance().getReference("taskList").child(UserData.getEmail().encodeEmail())

    private val listaImagenes: MutableList<String> = mutableListOf()

    private val adapter = ImageAdapter(listaImagenes) { previewImage(it) }

    private lateinit var binding: ActivityChangeImageBinding

    private var imageOnPreview: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChangeImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setListeners()
        setRecycler()
    }

    private fun setListeners() {
        binding.searchImage.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                showImages(query.toString().lowercase().trim())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
        binding.btnBack.setOnClickListener {
            if (imageOnPreview != "") {
                database.child("imagen").setValue(imageOnPreview).addOnSuccessListener {
                        Toast.makeText(
                            this,
                            "La imagen de perfil cambiada con éxito",
                            Toast.LENGTH_SHORT
                        ).show()
                    UserData.setImage(imageOnPreview)
                    }.addOnFailureListener {
                        Toast.makeText(
                            this,
                            "La imagen de perfil no ha sido cambiada con éxito",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
            finish()
        }
    }

    private fun setRecycler() {
        val layout = GridLayoutManager(this, 2)
        binding.recyclerImages.layoutManager = layout

        binding.recyclerImages.adapter = adapter
    }

    private fun previewImage(image: String) {
        imageOnPreview = image
        Picasso.get().load(imageOnPreview).into(binding.perfilImagen)
    }

    private fun showImages(busqueda: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = ApiImage.api.getImages(busqueda, getString(R.string.api_key_uplash))

                if (response.isSuccessful) {
                    val lista = response.body()?.results?.map { it.urls.small } ?: emptyList()

                    withContext(Dispatchers.Main) {
                        if (lista.isNotEmpty()) {
                            adapter.updateImages(lista)
                        } else {
                            Toast.makeText(this@CambiarImagenActivity, "No se encontraron imágenes", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Log.e("API_ERROR", "Error ${response.code()}: ${response.message()}")
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@CambiarImagenActivity, "Error en la respuesta: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@CambiarImagenActivity, "Error al cargar iconos: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}