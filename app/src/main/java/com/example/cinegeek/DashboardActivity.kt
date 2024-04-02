package com.example.cinegeek

import SliderAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.MoviesViewModel
import com.example.cinegeek.databinding.ActivityDashboardBinding
import com.example.models.Movies
import com.example.models.Result
import com.example.models.Slider
import dagger.hilt.android.AndroidEntryPoint
import java.util.Timer
import java.util.TimerTask

@AndroidEntryPoint
class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding
    lateinit var viewModel : MoviesViewModel
    private var Movies : Movies?=null
    private lateinit var timer: Timer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val data = listOf(
            Slider(R.drawable.slider_img1),
            Slider(R.drawable.slider_img2),
            Slider(R.drawable.slider_img3),
            Slider(R.drawable.slider_img4),
            Slider(R.drawable.slider_img5)
        )

        val adapterSlider = SliderAdapter(data)
        binding?.vpSlider?.adapter = adapterSlider

        timer = Timer()
        timer.scheduleAtFixedRate(SliderTask(), 3000, 3000)

        viewModel = ViewModelProvider(this)[MoviesViewModel::class.java]
        setObservers()
    }

    inner class SliderTask : TimerTask() {
        override fun run() {
            runOnUiThread {
                binding?.vpSlider?.currentItem = (binding?.vpSlider?.currentItem?.plus(1))?.rem(
                    binding?.vpSlider?.adapter?.itemCount!!
                )!!
            }
        }
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