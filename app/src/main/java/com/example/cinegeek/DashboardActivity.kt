package com.example.cinegeek

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.MoviesViewModel
import com.example.cinegeek.databinding.ActivityDashboardBinding
import com.example.models.Movies
import com.example.models.Result
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    lateinit var viewModel : MoviesViewModel
    private var Movies : Movies?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MoviesViewModel::class.java]
        setObservers()
    }

    private fun setObservers() {
        viewModel.popularMoviesResponse.observe(this) {
            if (it != null) {
                Movies = it
                Log.e("setObservers", "$it")
                setUpRv(R.id.rvPopularMovies)
            }
        }

        viewModel.topRatedMoviesResponse.observe(this) {
            if (it != null) {
                Movies = it
                Log.e("setObservers", "$it")
                setUpRv(R.id.rvTopRated)
            }
        }

        viewModel.upcomingMoviesResponse.observe(this) {
            if (it != null) {
                Movies = it
                Log.e("setObservers", "$it")
                setUpRv(R.id.rvUpcoming)
            }
        }
    }


    fun setUpRv(rvId:Int){
        var popularMoviesList:MutableList<Result> = arrayListOf()

        for(i in 0..Movies?.results?.size!!-1){
            try {
                popularMoviesList.add(Movies?.results!![i])
            }catch (e:Exception){
                Log.e("Error",e.toString())
            }

        }
        val adapterUp=MovieAdapter(this,popularMoviesList)

        try {
            val rvPopular =findViewById<RecyclerView>(rvId)
            rvPopular.layoutManager= LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
            rvPopular.adapter=adapterUp
        }catch (e:Exception){
            Log.e("error",e.toString())
        }

        Log.e("Done","Done")
    }
}