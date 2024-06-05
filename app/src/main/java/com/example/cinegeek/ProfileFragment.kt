package com.example.cinegeek

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.MoviesViewModel
import com.example.cinegeek.databinding.FragmentExploreBinding
import com.example.cinegeek.databinding.FragmentProfileBinding
import com.example.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    lateinit var viewModel : MoviesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val view = binding.root

        viewModel = ViewModelProvider(this)[MoviesViewModel::class.java]

        viewModel.getUser(FirebaseAuth.getInstance().currentUser?.uid!!)
        setObservers()

        binding?.cvChangePfp?.setOnClickListener(){
            val intent = Intent()
            intent.action=Intent.ACTION_GET_CONTENT
            intent.type="image/*"
            startActivityForResult(Intent.createChooser(intent,"Select an image"),100)
        }

        binding?.tvLogOut?.setOnClickListener(){
            FirebaseAuth.getInstance().signOut()
            val intent= Intent(context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        return view
    }

    private fun setObservers() {
        viewModel.getUserResponse.observe(viewLifecycleOwner){
            if(it!=null){
                binding?.tvEmail?.text=it.email
                binding?.tvName?.text=it.name
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                binding?.tvJoinedOn?.text=sdf.format(FirebaseAuth.getInstance().currentUser?.metadata?.creationTimestamp).toString()
                binding?.tvUsername?.text="@"+it.username
                if (it.pfp.isNotEmpty()){
                    Glide.with(requireContext())
                        .load(it.pfp)
                        .into(binding.ivProfilePic)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == AppCompatActivity.RESULT_OK && data != null && data.data != null) {
            val imageUri = data.data
            Log.d("Error",imageUri.toString())
            Handler(Looper.getMainLooper()).post {


                val fileUri = data.data
                viewModel.addPfp(fileUri!!)

            }
        }
    }

}