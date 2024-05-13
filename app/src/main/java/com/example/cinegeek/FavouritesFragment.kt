package com.example.cinegeek

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.MoviesViewModel
import com.example.cinegeek.databinding.FragmentFavouritesBinding
import com.example.models.FavouriteModel
import com.example.models.MovieDetails
import com.example.models.Movies
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FavouritesFragment : Fragment() {
    private var _binding: FragmentFavouritesBinding? = null
    private val binding get() = _binding!!
    lateinit var viewModel : MoviesViewModel
    private var Movies : List<MovieDetails> = mutableListOf()
    private var FavouritesList : List<FavouriteModel>?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        val view = binding.root

        viewModel = ViewModelProvider(this)[MoviesViewModel::class.java]


        setObservers()
        viewModel.getFavourites()

        return view
    }

    private fun setObservers() {
        viewModel.fetchFavouritesResponse.observe(viewLifecycleOwner) { favourites ->
            if (favourites.isNotEmpty()) {
                FavouritesList = favourites
                viewModel.movieDetailsResponse.observe(viewLifecycleOwner) { movieDetails ->
                    if (movieDetails != null) {
                        Movies = Movies.plus(movieDetails)
                        if (Movies.size == FavouritesList!!.size) {
                            val favouritesListAdapter = FavouritesListAdapter(requireContext(), Movies as MutableList<MovieDetails>)
                            binding?.rvFavourites?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                            binding?.rvFavourites?.adapter = favouritesListAdapter
                        }
                    }
                }
                for (favourite in FavouritesList!!) {
                    viewModel.getMovieDetails(favourite.movieId)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getFavourites()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}