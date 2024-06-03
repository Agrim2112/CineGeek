package com.example.cinegeek

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.MoviesViewModel
import com.example.cinegeek.databinding.ItemSearchResultBinding
import com.example.models.MovieDetails
import com.example.models.Result
import kotlin.math.pow
import kotlin.math.round

class FavouritesListAdapter(private val context: Context, private var MoviesList: MutableList<MovieDetails>) : RecyclerView.Adapter<FavouritesListAdapter.ViewHolder>() {

    lateinit var viewModel : MoviesViewModel
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

    fun submitList(newFavourites: List<MovieDetails>) {
        MoviesList = ArrayList(newFavourites) // Create a new list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return MoviesList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if(position<0 || position>=MoviesList.size){
            return
        }
        val model = MoviesList[position]

        val trimmedText = if (model.title.length > 28) {
            model.title.substring(0, 24) + "..."
        } else {
            model.title
        }

        holder?.movieName?.text=trimmedText
        Glide.with(context)
            .load("https://image.tmdb.org/t/p/w200"+model.poster_path)
            .into(holder.movieImage)
        if (model.vote_average==0.0) {
            holder?.movieRating?.text = "N/A"
        }
        else {
            holder?.movieRating?.text = roundOff(model.vote_average, 1).toString()
        }


        holder.itemView.setOnClickListener{
            val intent= Intent(context,DetailsActivity::class.java)
            intent.putExtra("movieId",model.id.toString())
            context.startActivity(intent)
        }

        var movieGenres = ""
        if(model.genres.isEmpty()){
            movieGenres = "No genres"
        }
        else {
            for (i in 0..model.genres.size - 2) {
                movieGenres += model.genres[i].name + ", "
            }
        }
        if (model.genres.isNotEmpty()) {
            movieGenres += model.genres[model.genres.size-1].name
        }
        val trimmedGenres = if (movieGenres.length > 28) {
            movieGenres.substring(0, 24) + "..."
        } else {
            movieGenres
        }

        holder?.movieGenre?.text=trimmedGenres
    }

    fun roundOff(double: Double, decimalPoints: Int): Double {
        val multiplier = 10.0.pow(decimalPoints)
        return round(double * multiplier) / multiplier
    }

}