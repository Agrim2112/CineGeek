package com.example.cinegeek

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cinegeek.databinding.ItemMoviesBinding
import com.example.models.Movies
import com.example.models.Result

class MovieAdapter(private val context: Context, private val MoviesList: MutableList<Result>) : RecyclerView.Adapter<MovieAdapter.ViewHolder>() {

    class ViewHolder(binding: ItemMoviesBinding) : RecyclerView.ViewHolder(binding.root){
        var movieName = binding.tvMovie
        val movieImage = binding.ivMovie
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemMoviesBinding.inflate(
                LayoutInflater.from(parent.context),parent,false
            )
        )
    }

    override fun getItemCount(): Int {
        return MoviesList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = MoviesList[position]


        holder?.movieName?.text=model.title
        Glide.with(context)
            .load("https://image.tmdb.org/t/p/w200"+model.poster_path)
            .into(holder.movieImage)


        holder.itemView.setOnClickListener{
            val intent= Intent(context,DetailsActivity::class.java)
            intent.putExtra("movieId",model.id.toString())
            context.startActivity(intent)
        }
    }

}