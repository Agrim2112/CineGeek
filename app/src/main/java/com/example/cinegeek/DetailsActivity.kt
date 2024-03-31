package com.example.cinegeek

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.MoviesViewModel
import com.example.cinegeek.databinding.ActivityDetailsBinding
import com.example.models.MovieDetails
import com.example.models.Movies
import com.example.models.Result
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsActivity : AppCompatActivity() {
    private var binding :ActivityDetailsBinding?=null
    lateinit var viewModel : MoviesViewModel
    lateinit var movieDetails: MovieDetails
    private var similarMovies : Movies?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val receivedIntent = intent
        var movieId = receivedIntent.getStringExtra("movieId")

        viewModel = ViewModelProvider(this)[MoviesViewModel::class.java]

        viewModel.getMovieDetails(movieId!!)
        viewModel.getSimilarMovies(movieId!!)

        setObservers()
    }

    private fun setObservers() {

        viewModel.similarMovieListResponse.observe(this) {
            if (it != null) {
                similarMovies = it
                Log.e("setObservers", "$it")
                setUpRv()
            }
        }

        viewModel.movieDetailsResponse.observe(this,){
            if(it!=null)
            {
                binding?.tvTitle?.text= it.title
                binding?.tvPlot?.text=it.overview
                Glide.with(this)
                    .load("https://image.tmdb.org/t/p/w500"+it.poster_path)
                    .into(binding?.ivCover!!)
            }
        }
    }

    private fun setUpRv(){

        Log.e("setUpRv", "Similar movies list size:")

        var similarMoviesList:MutableList<Result> = arrayListOf()

        Log.e("setUpRv", "Similar movies list size: ${similarMoviesList.size}")

        for(i in 0..similarMovies?.results?.size!!-1){
            try {
                Log.e("setUpRv", "Similar movies list size: ${similarMoviesList.size}")
                similarMoviesList.add(similarMovies?.results!![i])
            }catch (e:Exception){
                Log.e("Error",e.toString())
            }

        }

        Log.e("setUpRv", "Similar movies list size: ${similarMoviesList.size}")

        val adapterUp = MovieAdapter(this, similarMoviesList)

        try {
            binding?.rvSimilar?.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
            binding?.rvSimilar?.adapter = adapterUp
        } catch (e: Exception) {
            Log.e("error", e.toString())
        }

        Log.e("setUpRv", "Done")
    }
}