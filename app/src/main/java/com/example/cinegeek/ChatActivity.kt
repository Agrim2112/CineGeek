package com.example.cinegeek

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.MoviesViewModel
import com.example.cinegeek.databinding.ActivityChatBinding
import com.example.models.Chat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatActivity : AppCompatActivity() {

    lateinit var viewModel : MoviesViewModel
    private  lateinit var binding: ActivityChatBinding
    lateinit var receiverId : String
    lateinit var profilePic : String
    lateinit var chatList:MutableList<Chat>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MoviesViewModel::class.java]

        val receivedIntent = intent
        receiverId = receivedIntent.getStringExtra("receiverId").toString()

        viewModel.getUser(receiverId!!)
        viewModel.getChat(FirebaseAuth.getInstance().currentUser?.uid!!,receiverId!!)


        binding?.btnBack?.setOnClickListener(){
            onBackPressed()
        }

        binding?.llViewProfile?.setOnClickListener(){
            val intent= Intent(this,ViewProfileActivity::class.java)
            intent.putExtra("receiverId",receiverId)
            startActivity(intent)
        }


        binding?.etMessage?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                if (s.toString().isEmpty()) {
                    viewModel.setTyping(false,receiverId)
                    binding?.ivCamera?.isEnabled = true
                    binding?.tvSend?.isEnabled = false
                    binding?.ivCamera?.setColorFilter(
                        ContextCompat.getColor(
                            this@ChatActivity,
                            R.color.cyan
                        )
                    )
                    binding?.tvSend?.setTextColor(
                        ContextCompat.getColor(
                            this@ChatActivity,
                            R.color.grey
                        )
                    )
                } else {
                    viewModel.setTyping(true,receiverId)
                    binding?.tvSend?.isEnabled = true
                    binding?.ivCamera?.isEnabled = false
                    binding?.ivCamera?.setColorFilter(
                        ContextCompat.getColor(
                            this@ChatActivity,
                            R.color.grey
                        )
                    )
                    binding?.tvSend?.setTextColor(
                        ContextCompat.getColor(
                            this@ChatActivity,
                            R.color.cyan
                        )
                    )
                }
            }
        })

        binding?.tvSend?.setOnClickListener(){
            val message=binding.etMessage.text.toString()
            viewModel.sendMessage(receiverId!!,message)
            binding.etMessage.setText("")
        }

        binding?.ivCamera?.setOnClickListener(){
            val intent = Intent()
            intent.action=Intent.ACTION_GET_CONTENT
            intent.type="image/*"
            startActivityForResult(Intent.createChooser(intent,"Select an image"),100)
        }

        setObservers()
}

    private fun setObservers() {
        viewModel.getUserResponse.observe(this){
            if(it!=null)
            {
                binding?.tvReceiver?.text=it.name
                profilePic=it.pfp
                if (profilePic.isNotEmpty()){
                    Glide.with(this)
                        .load(profilePic)
                        .into(binding.ivProfilePic)
                }
            }

        }
        viewModel.getChatsResponse.observe(this){
            if (it!=null)
            {
                chatList= it.toMutableList()
                Log.d("chats",it.toString())
                val llm=LinearLayoutManager(this)
                llm.stackFromEnd=true
                binding?.rvChats?.layoutManager=llm
                val chatAdapter=ChatAdapter(this,chatList,profilePic!!){
                        chat ->
                    val currentUser=FirebaseAuth.getInstance().currentUser?.uid!!
                    if (chat.sender != currentUser && !chat.seen) {
                        val chatReference = FirebaseDatabase.getInstance().getReference("Chat")
                        chatReference.child(chat.messageId).child("seen").setValue(true)
                    }
                }
                binding?.rvChats?.adapter=chatAdapter
                chatAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                        binding?.rvChats?.post {
                            binding?.rvChats?.smoothScrollToPosition(chatAdapter.itemCount - 1)
                        }
                    }
                })
            }
        }


    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK && data != null && data.data != null) {
                val imageUri = data.data
                Log.d("Error",imageUri.toString())
                Handler(Looper.getMainLooper()).post {


                    val fileUri = data.data
                    viewModel.sendImageMessage(receiverId,fileUri!!)

            }
        }
    }

    override fun onBackPressed() {
        // Refresh the chat list
        viewModel.getChatList()

        // Call the super method to handle the back button press
        super.onBackPressed()
    }
}