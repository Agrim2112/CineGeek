package com.example.cinegeek

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.cinegeek.databinding.ActivityLoginBinding
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.OAuthProvider
import java.util.Arrays

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth:FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var callbackManager: CallbackManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding?.ivTwitter?.setOnClickListener() {
            val provider = OAuthProvider.newBuilder("twitter.com")
            provider.addCustomParameter("lang", "en")

            val pendingResultTask = firebaseAuth.pendingAuthResult
            if (pendingResultTask != null) {
                pendingResultTask
                    .addOnSuccessListener {
                        startActivity(Intent(this, DashboardActivity::class.java))
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    }
            } else {
                firebaseAuth
                    .startActivityForSignInWithProvider(this, provider.build())
                    .addOnSuccessListener {
                        startActivity(Intent(this, DashboardActivity::class.java))
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    }
            }

            val firebaseUser = firebaseAuth.currentUser!!
            firebaseUser
                .startActivityForLinkWithProvider(this, provider.build())
                .addOnSuccessListener {
                    startActivity(Intent(this, DashboardActivity::class.java))
                }
                .addOnFailureListener {
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
        }

        callbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    Log.d(ContentValues.TAG, "facebook:onSuccess:$loginResult")
                    handleFacebookAccessToken(loginResult.accessToken)
                }

                override fun onCancel() {
                    Log.d(ContentValues.TAG, "facebook:onCancel")
                }

                override fun onError(error: FacebookException) {
                    Log.d(ContentValues.TAG, "facebook:onError", error)
                }
            })

        firebaseAuth=FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this , gso)

        binding?.ivGoogle?.setOnClickListener() {
            signInGoogle()
        }

        binding?.ivFacebook?.setOnClickListener(){
            LoginManager.getInstance().logInWithReadPermissions(this@LoginActivity, Arrays.asList("public_profile"));
        }

        binding?.tvSignUp?.setOnClickListener(){
            val intent= Intent(this,SignUpActivity::class.java)
            startActivity(intent)
        }

        binding?.tvForgotPwd?.setOnClickListener(){
            val intent= Intent(this,ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        binding?.btnSignup?.setOnClickListener(){
            val email=binding?.etUsername?.text.toString()
            val pwd=binding?.etPassword?.text.toString()

            if(email.isNotEmpty() && pwd.isNotEmpty()){
                firebaseAuth.signInWithEmailAndPassword(email,pwd).addOnCompleteListener {
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
        if (task.isSuccessful){
            val account : GoogleSignInAccount? = task.result
            if (account != null){
                updateUI(account)
            }
        }else{
            Toast.makeText(this, task.exception.toString() , Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI(account: GoogleSignInAccount?) {
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
    }

    private fun updateUI2(user: FirebaseUser?) {
        if (user != null) {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
        }
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(ContentValues.TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(ContentValues.TAG, "signInWithCredential:success")
                    val user = firebaseAuth.currentUser
                    updateUI2(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(ContentValues.TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    updateUI2(null)
                }
            }
    }
}