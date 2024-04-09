package com.example.cinegeek

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.cinegeek.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebase:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebase=FirebaseAuth.getInstance()

        binding?.tvSignUp?.setOnClickListener(){
            val intent= Intent(this,SignUpActivity::class.java)
            startActivity(intent)
        }

        binding?.btnSignup?.setOnClickListener(){
            val email=binding?.etUsername?.text.toString()
            val pwd=binding?.etPassword?.text.toString()

            if(email.isNotEmpty() && pwd.isNotEmpty()){
                firebase.signInWithEmailAndPassword(email,pwd).addOnCompleteListener {
                    if (it.isSuccessful) {
                    val intent = Intent(this, DashboardActivity::class.java)
                    startActivity(intent)
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else{
                Toast.makeText(this,"Please fill out all the fields",Toast.LENGTH_SHORT).show()
            }
        }

    }
}