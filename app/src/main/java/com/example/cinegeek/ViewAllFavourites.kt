package com.example.cinegeek

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.MoviesViewModel
import com.example.cinegeek.databinding.ActivityViewAllFavouritesBinding
import com.example.models.MovieDetails
import com.example.models.Movies
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.min

@AndroidEntryPoint
class ViewAllFavourites : AppCompatActivity() {
    private lateinit var binding:ActivityViewAllFavouritesBinding
    private lateinit var viewModel: MoviesViewModel
    private var Movies : List<MovieDetails> = mutableListOf()
    lateinit var FavouritesList: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityViewAllFavouritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel=ViewModelProvider(this)[MoviesViewModel::class.java]

        FavouritesList = intent.getStringArrayListExtra("favouritesList")!!
        val title = intent.getStringExtra("extraString")

        binding?.tvTitle?.text=title
        for (favourite in FavouritesList!!) {
            viewModel.getMovieDetails(favourite)
        }

        binding?.btnBack?.setOnClickListener(){
            onBackPressed()
        }
        setObservers()
    }

    private fun setObservers() {
        viewModel.movieDetailsResponse.observe(this) { movieDetails ->
            if (movieDetails != null) {
                Movies = Movies.plus(movieDetails)
                if (Movies.size == FavouritesList!!.size) {
                    binding?.rvFavourites?.visibility= View.VISIBLE
                    val favouritesListAdapter = FavouritesListAdapter(this, Movies as MutableList<MovieDetails>)
                    favouritesListAdapter.submitList(Movies)
                    binding?.rvFavourites?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                    binding?.rvFavourites?.adapter = favouritesListAdapter
                }
            }
        }
    }
}