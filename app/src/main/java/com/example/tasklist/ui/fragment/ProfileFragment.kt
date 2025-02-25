package com.example.tasklist.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.tasklist.R
import com.example.tasklist.data.UserData
import com.squareup.picasso.Picasso

class ProfileFragment(
    private val signOut: () -> Unit,
    private val activityAddCategorias: () -> Unit,
    private val activityCambiarImagen: () -> Unit
    ) : Fragment() {

    private lateinit var btnAddCategory: Button

    private lateinit var btnSignOut: Button

    private lateinit var btnCambiarImagen: Button

    private lateinit var textUsername: TextView

    private lateinit var perfilImagen: de.hdodenhof.circleimageview.CircleImageView

    private lateinit var btnMostrarVideo: Button

    private lateinit var btnBorrarTareas: Button



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setViews(view)
        setListeners()
    }

    override fun onResume() {
        super.onResume()
        setDatosUsuario()
    }

    private fun setDatosUsuario() {
        if(UserData.getImage() != ""){
            Picasso.get().load(UserData.getImage()).into(perfilImagen)
        }
        textUsername.text = UserData.getName()

    }

    private fun setViews(view: View) {
        btnMostrarVideo = view.findViewById(R.id.btn_mostrar_video)
        perfilImagen = view.findViewById(R.id.perfil_imagen)
        btnAddCategory = view.findViewById(R.id.btn_add_category)
        btnSignOut = view.findViewById(R.id.btn_logout)
        btnCambiarImagen = view.findViewById(R.id.btn_cambiar_imagen)
        textUsername = view.findViewById(R.id.tv_username)
        btnBorrarTareas = view.findViewById(R.id.btn_delete_tasks)
    }

    private fun setListeners() {
        btnAddCategory.setOnClickListener {
            activityAddCategorias()
        }
        btnSignOut.setOnClickListener {
            signOut()
        }
        btnCambiarImagen.setOnClickListener {
            activityCambiarImagen()
        }
        btnMostrarVideo.setOnClickListener {

        }
        btnBorrarTareas.setOnClickListener {

        }
    }

}