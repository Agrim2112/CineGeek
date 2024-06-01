package com.example.cinegeek

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.MoviesViewModel
import com.example.cinegeek.databinding.ActivityDetailsBinding
import com.example.models.Backdrop
import com.example.models.Cast
import com.example.models.UserFavouriteModel
import com.example.models.MovieCast
import com.example.models.MovieDetails
import com.example.models.MovieImages
import com.example.models.Movies
import com.example.models.Result
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.pow
import kotlin.math.round

@AndroidEntryPoint
class DetailsActivity : AppCompatActivity() {
    private var binding :ActivityDetailsBinding?=null
    lateinit var viewModel : MoviesViewModel
    lateinit var movieDetails: MovieDetails
    private var similarMovies : Movies?=null
    private var castDetails:MovieCast?=null
    private var movieImages:MovieImages?=null
    private var movieGenres:String=""
    private var isFavourite:Boolean=false

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
        viewModel.getMovieImages(movieId!!)
        viewModel.checkFavourites(movieId!!)


        binding?.llMatch?.setOnClickListener(){
            val intent= Intent(this,MatchActivity::class.java)
            intent.putExtra("movieId",movieId)
            startActivity(intent)
        }

        binding?.favoriteButton?.setOnCheckedChangeListener {_, isChecked ->
            if(isChecked) {
                viewModel.addToFavourites(FirebaseAuth.getInstance().uid!!, movieId)
                Log.d("Display Name",FirebaseAuth.getInstance().currentUser?.displayName!!)
            }
            else
                viewModel.removeFromFavourites(FirebaseAuth.getInstance().uid!!,movieId)
        }

        setObservers()
    }

    private fun setObservers() {


        viewModel.isFavouriteResponse.observe(this){
            if(it!=null)
            {
                isFavourite=it
                binding?.favoriteButton?.isChecked=isFavourite
            }
        }
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

        viewModel.movieImagesResponse.observe(this){
            if(it!=null)
            {
                movieImages=it
                Log.e("setObservers", "$it")
                setUpImagesRv()
            }
        }

        viewModel.movieDetailsResponse.observe(this,){
            if(it!=null)
            {
                binding?.llContent?.visibility=View.VISIBLE
                binding?.loadingAnimation?.visibility=View.GONE
                binding?.tvTitle?.text= it.title
                binding?.tvPlot?.text=it.overview
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                val date = inputFormat.parse(it.release_date)
                val formattedDate = outputFormat.format(date)
                binding?.tvReleaseDate?.text=formattedDate
                Glide.with(this)
                    .load("https://image.tmdb.org/t/p/w500"+it.poster_path)
                    .into(binding?.ivCover!!)

                for(i in 0..it.genres.size-2)
                {
                    movieGenres+=it.genres[i].name+", "

                }
                movieGenres+=it.genres[it.genres.size-1].name

                binding?.tvGenres?.text=movieGenres
                binding?.tvRuntime?.text=(it.runtime/60).toString() +"h "+ (it.runtime%60).toString()+"m"
                binding?.tvRating?.text=roundOff(it.vote_average, 1).toString()
            }
        }
    }

    fun roundOff(double: Double, decimalPoints: Int): Double {
        val multiplier = 10.0.pow(decimalPoints)
        return round(double * multiplier) / multiplier
    }
    private fun setUpImagesRv() {
        var movieImagesList : MutableList<Backdrop> = arrayListOf()

        var size:Int =10

        if(movieImages?.backdrops?.size!!-1 <10)
            size = movieImages?.backdrops?.size!!-1

        for (i in 0..size)
        {
            try {
                movieImagesList.add(movieImages?.backdrops!![i])
            } catch (e:Exception){
                Log.e("error",e.toString())
            }
        }

        val adapterImages = ImagesAdapter(this,movieImagesList)

        try {
            binding?.rvImages?.layoutManager=LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
            binding?.rvImages?.adapter=adapterImages
        }catch  (e:Exception){
            Log.e("Error",e.toString())
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