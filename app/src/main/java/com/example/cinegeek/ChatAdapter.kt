package com.example.cinegeek

import android.content.Context
import android.content.Intent
import android.renderscript.ScriptGroup.Binding
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.BindingMethod
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.MoviesViewModel
import com.example.cinegeek.databinding.ItemImagesBinding
import com.example.cinegeek.databinding.ItemProfileBinding
import com.example.cinegeek.databinding.LeftChatBinding
import com.example.cinegeek.databinding.RightChatBinding
import com.example.models.Chat
import com.example.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.makeramen.roundedimageview.RoundedImageView

class ChatAdapter(private val context: Context, private val chatList: MutableList<Chat>,val imageUrl:String,private val onMessageSeen: (Chat) -> Unit) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    lateinit var viewModel : MoviesViewModel
    val currentUser =FirebaseAuth.getInstance().currentUser?.uid!!
    var lastSeenMessage:Chat?=null
    init {
        for (i in chatList.size - 1 downTo 0) {
            val chat = chatList[i]
            if (chat.sender == currentUser && chat.seen==true) {
                lastSeenMessage = chat
                break
            }
        }
    }
    class ViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView){
        val profilePic=itemView.findViewById<RoundedImageView>(R.id.ivLeftProfilePic)
        val message=itemView.findViewById<TextView>(R.id.tvMessage)
        val seen= itemView.findViewById<TextView>(R.id.tvSeen)
        val leftImage = itemView.findViewById<RoundedImageView>(R.id.ivLeftImage)
        val rightImage = itemView.findViewById<RoundedImageView>(R.id.ivRightImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
        return if(viewType==1) {
            val view:View=LayoutInflater.from(context).inflate(R.layout.right_chat,parent,false)
            ViewHolder(view)
        } else {
            val view:View=LayoutInflater.from(context).inflate(R.layout.left_chat,parent,false)
            ViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = chatList[position]

        onMessageSeen(model)

        if(model==lastSeenMessage){
            holder?.seen?.visibility=View.VISIBLE
        }

        if(model.message.equals("Image") && !model.url.equals("")){
            holder?.message?.visibility=View.GONE
            if(model.receiver.equals(currentUser)){
                if(position<chatList.size-1 && (model.receiver==currentUser && chatList[position+1].receiver==currentUser)){
                    holder?.profilePic?.visibility=View.INVISIBLE
                }

                holder?.leftImage?.visibility=View.VISIBLE
                Glide.with(context)
                    .load(model.url)
                    .into(holder.leftImage)
            }
            else{

                holder?.rightImage?.visibility=View.VISIBLE
                Glide.with(context)
                    .load(model.url)
                    .into(holder.rightImage)
            }
        }
        else {

            if(position<chatList.size-1 && (model.receiver==currentUser && chatList[position+1].receiver==currentUser)){
                holder?.profilePic?.visibility=View.INVISIBLE
            }

            holder?.message?.text=model.message

            if(model.receiver.equals(currentUser)){
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if(chatList[position].sender.equals(currentUser)) {
            1
        }
        else {
            0
        }
    }
}