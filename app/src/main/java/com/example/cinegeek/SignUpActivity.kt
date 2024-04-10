package com.example.cinegeek

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.cinegeek.databinding.ActivitySignUpBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth= FirebaseAuth.getInstance()


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this , gso)

        binding?.ivGoogle?.setOnClickListener() {
            signInGoogle()
        }

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

    private fun signInGoogle(){
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
        if (result.resultCode == Activity.RESULT_OK){

            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleResults(task)
        }
    }

    private fun handleResults(task: Task<GoogleSignInAccount>) {
        try {
            val account = task.getResult(ApiException::class.java)
            val auth = FirebaseAuth.getInstance()
            val email = account?.email
            val idToken = account?.idToken

            auth.fetchSignInMethodsForEmail(email!!).addOnCompleteListener { task ->
                val isNewUser = task.result?.signInMethods?.isEmpty() == true

                if (isNewUser) {
                    auth.createUserWithEmailAndPassword(email, idToken!!)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val user = auth.currentUser
                                updateUI(user)
                            } else {
                                Toast.makeText(this, "Failed to create new account: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                updateUI(null)
                            }
                        }
                }
            }
        } catch (e: ApiException) {
            Toast.makeText(this, "Google sign in failed: ${e.statusCode}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }
    }
}