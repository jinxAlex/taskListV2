package com.example.tasklist.ui.main.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tasklist.R
import com.example.tasklist.databinding.ActivityLoginBinding
import com.example.tasklist.ui.main.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth

class LoginActivity : AppCompatActivity() {

    private var email = ""

    private var pass = ""

    private val responseLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if(it.resultCode == RESULT_OK){
            val datos = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val cuenta = datos.getResult(ApiException::class.java)
                if(cuenta != null) {
                    val credenciales = GoogleAuthProvider.getCredential(cuenta.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credenciales)
                        .addOnCompleteListener {
                            if (it.isSuccessful)
                                irTareasActivity()
                        }
                        .addOnFailureListener{
                            Toast.makeText(this,it.message.toString(),Toast.LENGTH_SHORT)
                        }
                    }

                }catch (e: Exception){
                    Toast.makeText(this,"ERROR",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private  lateinit var binding: ActivityLoginBinding

    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = Firebase.auth
        setListeners()

    }

    private fun setListeners() {
        binding.btnSignup.setOnClickListener{
            irSignUpActivity()
        }
        binding.btnLogin.setOnClickListener{
            if(datosCorrectos()){
                login()
            }
        }
        binding.btnGoogle.setOnClickListener {
            loginGoogle()
        }
    }

    private fun loginGoogle() {
        val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_cliente_id))
            .requestEmail()
            .build()
        val googleClient = GoogleSignIn.getClient(this,googleConf)

        googleClient.signOut()

        responseLauncher.launch(googleClient.signInIntent)
    }

    private fun datosCorrectos(): Boolean{
        pass = binding.etPass.text.toString().trim()
        email = binding.etEmail.text.toString().trim()

        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.tfEmail.error = "ERROR: El email no es valido"
            return false
        }

        if(pass.length < 9){
            binding.tfPass.error = "ERROR: La contraseña debe tener al menos 9 carácteres"
            return false
        }

        return true
    }


    private fun login() {
        auth.signInWithEmailAndPassword(email,pass)
            .addOnCompleteListener {
                if(it.isSuccessful) irTareasActivity()
            }
            .addOnFailureListener {
                Toast.makeText(this,it.message.toString(), Toast.LENGTH_LONG).show()
            }
    }



    override fun onStart() {
        super.onStart()
        val usuario = auth.currentUser
        if(usuario != null) irTareasActivity()
    }

    private fun irTareasActivity() {
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }

    private fun irSignUpActivity() {
        val i = Intent(this, SignUpActivity::class.java).apply {
            putExtra("EMAIl",email)
        }
        startActivity(i)
    }
}