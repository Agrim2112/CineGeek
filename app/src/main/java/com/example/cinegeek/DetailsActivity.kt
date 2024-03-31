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
import com.example.models.Cast
import com.example.models.MovieCast
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
    private var castDetails:MovieCast?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val receivedIntent = intent
        var movieId = receivedIntent.getStringExtra("movieId")

        viewModel = ViewModelProvider(this)[MoviesViewModel::class.java]

        viewModel.getMovieDetails(movieId!!)
        viewModel.getSimilarMovies(movieId!!)
        viewModel.getMovieCast(movieId!!)

        setObservers()
    }

    private fun setObservers() {

        viewModel.similarMovieListResponse.observe(this) {
            if (it != null) {
                similarMovies = it
                Log.e("setObservers", "$it")
                setUpSimilarMoviesRv()
            }
        }

        viewModel.movieCastResponse.observe(this){
            if(it!=null)
            {
                castDetails=it
                Log.e("setObservers", "$it")
                setUpCastRv()
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

    private fun setUpCastRv() {
        var castDetailsList: MutableList<Cast> = arrayListOf()

        for(i in  0..castDetails?.cast?.size!!-1)
        {
            try {
                if (castDetails?.cast!![i].known_for_department == "Acting" && castDetails?.cast!![i].profile_path != null) {
                    castDetailsList.add(castDetails?.cast!![i])
                }
            }catch (e:Exception){
                Log.e("Error",e.toString())
            }
        }

        var adapterCast=CastAdapter(this,castDetailsList)

        try {
            binding?.rvCast?.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
            binding?.rvCast?.adapter=adapterCast
        }catch (e:Exception){
            Log.e("Error",e.toString())
        }

    }

    private fun setUpSimilarMoviesRv(){


        var similarMoviesList:MutableList<Result> = arrayListOf()

        for(i in 0..similarMovies?.results?.size!!-1){
            try {
                similarMoviesList.add(similarMovies?.results!![i])
            }catch (e:Exception){
                Log.e("Error",e.toString())
            }

        }

        val adapterSimilar = MovieAdapter(this, similarMoviesList)

        try {
            binding?.rvSimilar?.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
            binding?.rvSimilar?.adapter = adapterSimilar
        } catch (e: Exception) {
            Log.e("error", e.toString())
        }

        Log.e("setUpRv", "Done")
    }
}