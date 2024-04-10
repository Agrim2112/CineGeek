package com.example.cinegeek

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.cinegeek.databinding.ActivitySignUpBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)


        firebaseAuth= FirebaseAuth.getInstance()

        binding.tvSignUp.setOnClickListener()
        {
            val intent= Intent(this,LoginActivity::class.java)
            startActivity(intent)
        }

        binding?.btnSignup?.setOnClickListener(){
            val email=binding.etUsername.text.toString()
            val pwd = binding.etPassword.text.toString()

            if(email.isNotEmpty() && pwd.isNotEmpty())
            {
                firebaseAuth.createUserWithEmailAndPassword(email,pwd).addOnCompleteListener {
                    if(it.isSuccessful)
                    {
                        val intent=Intent(this,DashboardActivity::class.java)
                        startActivity(intent)
                    }
                    else
                    {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else{
                Toast.makeText(this,"Please fill all the fields",Toast.LENGTH_SHORT).show()
            }
        }
    }
}