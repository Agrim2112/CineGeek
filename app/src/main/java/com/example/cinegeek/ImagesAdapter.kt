package com.example.cinegeek

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cinegeek.databinding.ItemCastBinding
import com.example.cinegeek.databinding.ItemImagesBinding
import com.example.cinegeek.databinding.ItemMoviesBinding
import com.example.models.Backdrop
import com.example.models.Cast
import com.example.models.Movies
import com.example.models.Result

class ImagesAdapter(private val context: Context, private val movieImagesList: MutableList<Backdrop>) : RecyclerView.Adapter<ImagesAdapter.ViewHolder>() {

    class ViewHolder(binding: ItemImagesBinding) : RecyclerView.ViewHolder(binding.root){
        var image = binding.ivMovieImage
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemImagesBinding.inflate(
                LayoutInflater.from(parent.context),parent,false
            )
        )
    }

    override fun getItemCount(): Int {
        return movieImagesList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = movieImagesList[position]

        Glide.with(context)
            .load("https://image.tmdb.org/t/p/w200"+model.file_path)
            .into(holder.image)
    }

}