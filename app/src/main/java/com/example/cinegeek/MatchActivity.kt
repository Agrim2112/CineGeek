package com.example.cinegeek

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.MoviesViewModel
import com.example.cinegeek.databinding.ActivityMainBinding
import com.example.cinegeek.databinding.ActivityMatchBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MatchActivity : AppCompatActivity() {

    lateinit var viewModel : MoviesViewModel
    private lateinit var binding: ActivityMatchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMatchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val receivedIntent = intent
        var movieId = receivedIntent.getStringExtra("movieId")

        viewModel = ViewModelProvider(this)[MoviesViewModel::class.java]

        viewModel.getUserMatchList(movieId!!)
        setObservers()
    }

    private fun setObservers() {
        viewModel.MatchUserListResponse.observe(this){
            if (it!=null){
                val userList = it
                Log.d("Match",userList.toString())
                binding?.rvMatches?.layoutManager=LinearLayoutManager(this)
                binding?.rvMatches?.adapter=MatchAdapter(this,userList as MutableList)
            }
        }
    }
}