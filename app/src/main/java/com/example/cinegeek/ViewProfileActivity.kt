package com.example.cinegeek

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.MoviesViewModel
import com.example.cinegeek.databinding.ActivityViewProfileBinding
import com.example.models.MovieDetails
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.min

@AndroidEntryPoint
class ViewProfileActivity : AppCompatActivity() {
    private lateinit var binding:ActivityViewProfileBinding
    private lateinit var viewModel: MoviesViewModel
    private lateinit var receiverId:String
    private lateinit var currentUser:String
    private var Movies : List<MovieDetails> = mutableListOf()
    private var FavouritesList : List<String>?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentUser=FirebaseAuth.getInstance().currentUser?.uid!!

        val receivedIntent = intent
        receiverId = receivedIntent.getStringExtra("receiverId").toString()

        binding?.llMessage?.setOnClickListener(){
            val intent= Intent(this,ChatActivity::class.java)
            intent.putExtra("receiverId",receiverId)
            startActivity(intent)
        }
        viewModel=ViewModelProvider(this)[MoviesViewModel::class.java]

        viewModel.getFavourites(receiverId)
        viewModel.getUser(receiverId)

        setObservers()

        binding?.tvSeeAll?.setOnClickListener(){
            val intent = Intent(this, ViewAllFavourites::class.java)
            intent.putStringArrayListExtra("favouritesList", ArrayList(FavouritesList))
            intent.putExtra("extraString", "Favourites")
            startActivity(intent)
        }
    }

    private fun setObservers() {

        viewModel.getUserResponse.observe(this){
            if(it.pfp.isNotEmpty()){
                Glide.with(this)
                    .load(it.pfp)
                    .into(binding.ivProfilePic)
            }



            binding?.tvName?.text=it.name
            binding?.tvUsername?.text="@"+it.username
            if(it.bio.isNotEmpty()) {
                binding?.tvBio?.text = it.bio
            }
            else{
                binding?.tvBio?.visibility=View.GONE
            }
        }


        viewModel.fetchFavouritesResponse.observe(this) { favourites ->
            Log.d("View",favourites.toString())
            if (favourites!=null) {
                FavouritesList = favourites
                for (favourite in FavouritesList!!) {
                    viewModel.getMovieDetails(favourite)
                }
            }
        }

        viewModel.movieDetailsResponse.observe(this) { movieDetails ->
            if (movieDetails != null) {
                Movies = Movies.plus(movieDetails)
                if (Movies.size == FavouritesList!!.size) {
                    if(FavouritesList!!.size<=5){
                        binding?.tvSeeAll?.visibility=View.GONE
                    }
                    val favouritesListAdapter = FavouritesListAdapter(this, Movies.take(min(Movies!!.size,5)) as MutableList<MovieDetails>)
                    favouritesListAdapter.submitList(Movies.take(min(Movies!!.size,5)))
                    binding?.rvFavourites?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                    binding?.rvFavourites?.adapter = favouritesListAdapter
                }
            }
        }
    }

}