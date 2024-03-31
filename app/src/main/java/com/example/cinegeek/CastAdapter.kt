package com.example.cinegeek

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cinegeek.databinding.ItemCastBinding
import com.example.cinegeek.databinding.ItemMoviesBinding
import com.example.models.Cast
import com.example.models.Movies
import com.example.models.Result

class CastAdapter(private val context: Context, private val castDetailsList: MutableList<Cast>) : RecyclerView.Adapter<CastAdapter.ViewHolder>() {

    class ViewHolder(binding: ItemCastBinding) : RecyclerView.ViewHolder(binding.root){
        var actorImage = binding.ivActor
        val actorName = binding.tvActor
        var characterName = binding.tvCharacter
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCastBinding.inflate(
                LayoutInflater.from(parent.context),parent,false
            )
        )
    }

    override fun getItemCount(): Int {
        return castDetailsList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = castDetailsList[position]


        holder?.actorName?.text=model.name
        holder?.characterName?.text=model.character
        Glide.with(context)
            .load("https://image.tmdb.org/t/p/w200"+model.profile_path)
            .into(holder.actorImage)
    }

}