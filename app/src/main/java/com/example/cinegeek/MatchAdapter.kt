package com.example.cinegeek
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.MoviesViewModel
import com.example.cinegeek.databinding.ItemProfileBinding
import com.example.models.UserModel

class MatchAdapter(private val context: Context, private val UserList: MutableList<UserModel>) : RecyclerView.Adapter<MatchAdapter.ViewHolder>() {

    lateinit var viewModel : MoviesViewModel
    class ViewHolder(binding: ItemProfileBinding) : RecyclerView.ViewHolder(binding.root){
        var name = binding.tvName
        var profilePic = binding.ivProfile
        var username = binding.tvUsername
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemProfileBinding.inflate(
                LayoutInflater.from(parent.context),parent,false
            )
        )
    }

    override fun getItemCount(): Int {
        return UserList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        val model = UserList[position]

        val trimmedText = if (model.name.length > 28) {
            model.name.substring(0, 24) + "..."
        } else {
            model.name
        }

        if(model.pfp.isNotEmpty()){
            Glide.with(context)
                .load(model.pfp)
                .into(holder.profilePic)
        }

        holder?.name?.text=trimmedText

        holder.itemView.setOnClickListener{
            val intent= Intent(context,ViewProfileActivity::class.java)
            intent.putExtra("receiverId",model.uid)
            context.startActivity(intent)
        }


        val trimmedUsername = if (model.username.length > 28) {
            model.username.substring(0, 24) + "..."
        } else {
            model.username
        }

        holder?.username?.text="@"+trimmedUsername
    }



}