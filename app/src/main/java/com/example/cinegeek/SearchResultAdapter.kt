package com.example.cinegeek

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.MoviesViewModel
import com.example.cinegeek.databinding.ItemMoviesBinding
import com.example.cinegeek.databinding.ItemSearchResultBinding
import com.example.models.Result
import kotlin.math.pow
import kotlin.math.round

class SearchResultAdapter(private val context: Context, private val MoviesList: MutableList<Result>) : RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {

    lateinit var viewModel : MoviesViewModel
    var movieGenres:String=""
    class ViewHolder(binding: ItemSearchResultBinding) : RecyclerView.ViewHolder(binding.root){
        var movieName = binding.tvTitle
        var movieGenre = binding.tvGenre
        var movieRating = binding.tvRating
        val movieImage = binding.ivMovie
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSearchResultBinding.inflate(
                LayoutInflater.from(parent.context),parent,false
            )
        )
    }

    override fun getItemCount(): Int {
        return MoviesList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val model = MoviesList[position]
//        viewModel = ViewModelProvider(activity as DashboardActivity)[MoviesViewModel::class.java]
//
//        viewModel.getMovieDetails(model.id.toString()!!)

        val trimmedText = if (model.title.length > 17) {
            model.title.substring(0, 15) + "..."
        } else {
            model.title
        }

        holder?.movieName?.text=trimmedText
        Glide.with(context)
            .load("https://image.tmdb.org/t/p/w200"+model.poster_path)
            .into(holder.movieImage)
        holder?.movieRating?.text=roundOff(model.vote_average,1).toString()


        holder.itemView.setOnClickListener{
            val intent= Intent(context,DetailsActivity::class.java)
            intent.putExtra("movieId",model.id.toString())
            context.startActivity(intent)
        }

//        viewModel.movieDetailsResponse.observe(activity,){
//            if(it!=null)
//            {
//                for(i in 0..it.genres.size-2)
//                {
//                    movieGenres+=it.genres[i].name+", "
//
//                }
//                movieGenres+=it.genres[it.genres.size-1].name
//            }
//        }

        holder?.movieGenre?.text=model.original_language.uppercase()
    }

    fun roundOff(double: Double, decimalPoints: Int): Double {
        val multiplier = 10.0.pow(decimalPoints)
        return round(double * multiplier) / multiplier
    }

}