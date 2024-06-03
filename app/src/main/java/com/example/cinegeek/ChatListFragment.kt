package com.example.cinegeek

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.MoviesViewModel
import com.example.cinegeek.databinding.FragmentChatListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatListFragment : Fragment() {
    private var _binding: FragmentChatListBinding? = null
    private val binding get() = _binding!!
    lateinit var viewModel : MoviesViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChatListBinding.inflate(inflater, container, false)
        val view = binding.root

        viewModel = ViewModelProvider(this)[MoviesViewModel::class.java]

        setObservers()
        viewModel.getChatList()

        return view
    }

    private fun setObservers() {
        viewModel.chatListResponse.observe(viewLifecycleOwner){
            if(it!=null){
                var chatList=it
                chatList=chatList.sortedByDescending{ it.timestamp }
                binding?.rvChat?.layoutManager=LinearLayoutManager(context)
                val chatListAdapter = ChatListAdapter(requireContext(), chatList as MutableList)
                binding?.rvChat?.adapter = chatListAdapter
            }
        }
    }

    override fun onStop() {
        super.onStop()
        viewModel.removeChatListEventListener()
    }

    override fun onResume() {
        super.onResume()

        // Refresh the chat list
        viewModel.getChatList()
    }
}