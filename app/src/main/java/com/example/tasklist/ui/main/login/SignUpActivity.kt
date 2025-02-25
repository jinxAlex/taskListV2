package com.example.tasklist.ui.main.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tasklist.R
import com.example.tasklist.databinding.ActivitySignupBinding
import com.example.tasklist.model.User
import com.example.tasklist.ui.main.MainActivity
import com.example.tasklist.utils.encodeEmail
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {

    private var email = ""

    private var name = ""

    private var pass = ""

    private lateinit var binding: ActivitySignupBinding

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = Firebase.auth
        setListeners()
    }

    fun setListeners(){
        binding.btnSignup.setOnClickListener {
            registrar()
        }
    }

    private fun registrar() {
        if(datosCorrectos()){
            auth.createUserWithEmailAndPassword(email,pass)
                .addOnCompleteListener {
                    if(it.isSuccessful){
                        val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("taskList")
                        val item = User(name,"")
                        val nodo = email.encodeEmail()
                        Log.d("EMAIL", nodo)
                        database.child(nodo).setValue(item)
                            .addOnSuccessListener {
                                irTareasActivity()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this@SignUpActivity, "Ocurrió un error al guardar los datos", Toast.LENGTH_SHORT).show()
                            }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this,it.message.toString(), Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun datosCorrectos(): Boolean{
        name = binding.etName.text.toString().trim()
        pass = binding.etPass.text.toString().trim()
        email = binding.etEmail.text.toString().trim()
        
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.tfEmail.error = "ERROR: El email no es valido"
            return false
        }

        if(name.isEmpty()){
            binding.tfName.error = "ERROR: La contraseña no es valida"
            return false
        }

        if(pass.length < 9){
            binding.tfPass.error = "ERROR: La contraseña debe tener al menos 9 carácteres"
            return false
        }

            return true
    }

    private fun irTareasActivity() {
        val i = Intent(this, MainActivity::class.java).apply {
            putExtra("EMAIl",email)
        }
        startActivity(i)
    }
}