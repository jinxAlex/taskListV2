package com.example.tasklist.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.tasklist.R
import com.example.tasklist.data.UserData
import com.example.tasklist.databinding.ActivityMainBinding
import com.example.tasklist.model.User
import com.example.tasklist.ui.fragment.BrujulaFragment
import com.example.tasklist.ui.fragment.MapaFragment
import com.example.tasklist.ui.fragment.ProfileFragment
import com.example.tasklist.ui.fragment.TasksFragment
import com.example.tasklist.utils.encodeEmail
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.android.gms.maps.SupportMapFragment

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var map: GoogleMap

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permisos ->
        if (permisos[Manifest.permission.ACCESS_FINE_LOCATION] == true || permisos[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            mostrarUbicacion()
        } else {
            Toast.makeText(this, "ERROR: El usuario ha denegado los permisos", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private lateinit var binding: ActivityMainBinding

    // Fragments
    private val fragmentTask = TasksFragment()

    private val fragmentCompass = BrujulaFragment()

    private val fragmentProfile = ProfileFragment({signOut()},{irAddCategory()},{activityChangeImage()} )

    private fun activityChangeImage() {
        val i = Intent(this,CambiarImagenActivity::class.java)
        startActivity(i)
    }

    // Variables Firebase y Authentication
    private lateinit var database: DatabaseReference

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
        auth = Firebase.auth
        database= FirebaseDatabase.getInstance().getReference("taskList")
        setUserData()
        setListeners()
        setHome()
    }

    private fun setUserData() {
        UserData.setEmail(auth.currentUser?.email.toString())
        database.child(UserData.getEmail().encodeEmail()).get()
            .addOnSuccessListener { snapshot ->
                val userData = snapshot.getValue(User::class.java)
                UserData.setName(userData?.name.toString())
                UserData.setImage(userData?.imagen.toString())
            }
            .addOnFailureListener { exception ->
                Log.e("Firebase", "Error al obtener datos", exception)
            }

    }

    private fun setHome() {
        setFragment(fragmentTask)
    }

    private fun setBtnAddInvisible(){
        binding.btnAdd.visibility = INVISIBLE
        binding.btnAdd.isEnabled = false
    }

    private fun setListeners() {
        binding.bottomNavigation.setOnItemSelectedListener {
            setBtnAddInvisible()
            when(it.itemId){
                R.id.item_map ->{
                    setFragmentMapa()
                    true
                }
                R.id.item_compass ->{
                    setFragment(fragmentCompass)
                    true
                }
                R.id.item_home -> {
                    binding.btnAdd.visibility = VISIBLE
                    binding.btnAdd.isEnabled = true
                    setFragment(fragmentTask)
                    true
                }
                R.id.item_profile -> {
                    setFragment(fragmentProfile)
                    true
                }
                else -> false
            }
        }
        binding.btnAdd.setOnClickListener {
            irAddActivity()
        }
    }

    private fun irAddActivity(){
        ListCategoriaViewModel().listCategorias.observe(this@MainActivity) { categories ->
            val i = Intent(this,AddTaskActivity::class.java).apply {
                putExtra("CATEGORIAS",ArrayList(categories))
            }
            startActivity(i)
        }


    }

    private fun irAddCategory(){
        val i = Intent(this,AddCategoriaActivity::class.java)
        startActivity(i)
    }

    private fun setFragment(fg: Fragment){
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.fragment,fg)
        }
    }

    private fun setFragmentMapa(){
        val fragment = SupportMapFragment()
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.fragment,fragment)
        }
        fragment.getMapAsync(this)
    }

    private fun signOut(){
        auth.signOut()
        finish()
    }

    override fun onMapReady(p0: GoogleMap) {
        map = p0
        map.uiSettings.isZoomControlsEnabled = true
        map.mapType = GoogleMap.MAP_TYPE_HYBRID
        gestionarLocalizacion()
    }

    private fun mostrarUbicacion(){
        if(::map.isInitialized){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                ||
                ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                map.isMyLocationEnabled = true
                map.uiSettings.isMyLocationButtonEnabled = true
            }else{
                solicitarPermisos()
            }
        }
    }

    private fun gestionarLocalizacion() {
        if(!::map.isInitialized) return //:: al ser map de tipo lateinit
        if(
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
            &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ){
            map.isMyLocationEnabled = true
            map.uiSettings.isMyLocationButtonEnabled = true
        }else{
            //vamos a pedir los permisos
            solicitarPermisos()
        }
    }

    private fun solicitarPermisos() {
        if(
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
            ||
            ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
        ){
            mostrarExplicacion()
        }else{
            escogerPermisos()
        }
    }

    private fun escogerPermisos() {
        locationPermissionRequest.launch(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        )
    }

    private fun mostrarExplicacion() {
        AlertDialog.Builder(this).setTitle("Permisos de ubicación")
            .setMessage("Para el uso adecuado de esta incredible aplicación necesitamos los permisos de ubicación")
            .setNegativeButton("Cancelar"){
                    dialog,_-> dialog.dismiss()
            }
            .setCancelable(false)
            .setPositiveButton("Aceptar"){
                    dialog,_-> startActivity(Intent(Settings.ACTION_APPLICATION_SETTINGS))
            }
            .create()
            .show()
    }
}