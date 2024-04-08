package com.example.cinegeek

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cinegeek.databinding.ItemMoviesBinding
import com.example.cinegeek.databinding.ItemSearchResultBinding
import com.example.models.Result

class SearchResultAdapter(private val context: Context, private val MoviesList: MutableList<Result>) : RecyclerView.Adapter<SearchResultAdapter.ViewHolder>() {

    class ViewHolder(binding: ItemSearchResultBinding) : RecyclerView.ViewHolder(binding.root){
        var movieName = binding.tvTitle
        var movieGenre = binding.tvGenre
        var movieLanguage = binding.tvLanguage
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

        val trimmedText = if (model.title.length > 17) {
            model.title.substring(0, 15) + "..."
        } else {
            model.title
        }

        holder?.movieName?.text=trimmedText
        Glide.with(context)
            .load("https://image.tmdb.org/t/p/w200"+model.poster_path)
            .into(holder.movieImage)
        holder?.movieLanguage?.text=model.original_language.uppercase()


        holder.itemView.setOnClickListener{
            val intent= Intent(context,DetailsActivity::class.java)
            intent.putExtra("movieId",model.id.toString())
            context.startActivity(intent)
        }
    }

}