package com.example.cinegeek

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.MoviesViewModel
import com.example.cinegeek.databinding.FragmentFavouritesBinding
import com.example.models.UserFavouriteModel
import com.example.models.MovieDetails
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FavouritesFragment : Fragment() {
    private var _binding: FragmentFavouritesBinding? = null
    private val binding get() = _binding!!
    lateinit var viewModel : MoviesViewModel
    private var Movies : List<MovieDetails> = mutableListOf()
    private lateinit var currentUser:String
    private var FavouritesList : List<String>?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        val view = binding.root

        currentUser=FirebaseAuth.getInstance().currentUser?.uid!!

        viewModel = ViewModelProvider(this)[MoviesViewModel::class.java]

        setObservers()
        viewModel.getFavourites(currentUser)
        viewModel.getChatList()

        return view
    }

    private fun setObservers() {
        viewModel.fetchFavouritesResponse.observe(viewLifecycleOwner) { favourites ->
            Log.d("View",favourites.toString())
            if (favourites!=null) {
                FavouritesList = favourites
                for (favourite in FavouritesList!!) {
                    viewModel.getMovieDetails(favourite)
                }
            }
        }

        viewModel.movieDetailsResponse.observe(viewLifecycleOwner) { movieDetails ->
            if (movieDetails != null) {
                Movies = Movies.plus(movieDetails)
                if (Movies.size == FavouritesList!!.size) {
                    binding?.rvFavourites?.visibility=View.VISIBLE
                    binding?.loadingAnimation?.visibility=View.GONE
                    val favouritesListAdapter = FavouritesListAdapter(requireContext(), Movies as MutableList<MovieDetails>)
                    favouritesListAdapter.submitList(Movies)
                    binding?.rvFavourites?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                    binding?.rvFavourites?.adapter = favouritesListAdapter
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getFavourites(currentUser)
    }
    override fun onStop() {
        super.onStop()
        viewModel.removeFavListEventListener()
    }

}