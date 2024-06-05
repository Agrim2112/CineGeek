package com.example.cinegeek

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.MoviesViewModel
import com.example.cinegeek.databinding.ItemCastBinding
import com.example.cinegeek.databinding.ItemChatListBinding
import com.example.models.ReceiverChatList
import com.example.models.UserModel
import com.google.firebase.auth.FirebaseAuth

class ChatListAdapter(private val context: Context, private var chatList: MutableList<ReceiverChatList>) : RecyclerView.Adapter<ChatListAdapter.ViewHolder>() {

    lateinit var viewModel : MoviesViewModel
    val typeface = ResourcesCompat.getFont(context, R.font.roboto_bold)

    class ViewHolder(binding: ItemChatListBinding) : RecyclerView.ViewHolder(binding.root){
        var name = binding.tvName
        var profilePic = binding.ivProfile
        var chat = binding.tvChat
        var circle=binding.ivCircle
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemChatListBinding.inflate(
                LayoutInflater.from(parent.context),parent,false
            )
        )
    }


    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        val model = chatList[position]

        if(model.userInfo.pfp.isNotEmpty()){
            Glide.with(context)
                .load(model.userInfo.pfp)
                .into(holder.profilePic)
        }

        val trimmedName = if (model.userInfo.name.length > 40) {
            model.userInfo.name.substring(0, 36) + "..."
        } else {
            model.userInfo.name
        }

        holder?.name?.text= trimmedName

        holder.itemView.setOnClickListener{
            val intent= Intent(context,ChatActivity::class.java)
            intent.putExtra("receiverId",model.userInfo.uid)
            context.startActivity(intent)
        }


        var trimmedMessage="Typing..."
        if(model.typing==false) {
            trimmedMessage = if (model.chatInfo.message.length > 40) {
                model.chatInfo.message.substring(0, 36) + "..."
            } else {
                model.chatInfo.message
            }
        }

        holder?.chat?.text= trimmedMessage

        if(model?.chatInfo?.receiver==FirebaseAuth.getInstance().currentUser?.uid!! && model.chatInfo.seen==false || model.typing==true){
            holder?.chat?.typeface=typeface
            holder?.chat?.setTextColor(
                ContextCompat.getColor(
                context,
                R.color.cyan
            ))
            Log.d("State",model.chatInfo.seen.toString())
            holder?.circle?.visibility=View.VISIBLE
        }

    }



}