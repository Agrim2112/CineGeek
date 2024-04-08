package com.example.cinegeek

import SliderAdapter
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.MoviesViewModel
import com.example.cinegeek.databinding.FragmentExploreBinding
import com.example.models.Movies
import com.example.models.Result
import com.example.models.Slider
import dagger.hilt.android.AndroidEntryPoint
import java.util.Timer
import java.util.TimerTask


@AndroidEntryPoint
class ExploreFragment : Fragment() {
    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!
    lateinit var viewModel : MoviesViewModel
    private var Movies : Movies?=null
    var apiCallsFinished = 0
    private lateinit var timer: Timer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        val view = binding.root

        val data = listOf(
            Slider(R.drawable.slider_img1),
            Slider(R.drawable.slider_img2),
            Slider(R.drawable.slider_img3),
            Slider(R.drawable.slider_img4),
            Slider(R.drawable.slider_img5)
        )

        val adapterSlider = SliderAdapter(data)
        if(isAdded)
            binding.vpSlider.adapter = adapterSlider

        if(isAdded){
            binding?.llContent?.visibility=View.GONE
            binding?.loadingAnimation?.visibility=View.VISIBLE
        }

        timer = Timer()
        timer.scheduleAtFixedRate(SliderTask(), 3000, 3000)

        viewModel = ViewModelProvider(this)[MoviesViewModel::class.java]
        setObservers()

        if(isAdded) {
            binding?.etSearch?.setOnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val searchText = binding.etSearch.text.toString()
                    if (searchText.isNotEmpty()) {
                        val searchFragment = SearchFragment().apply {
                            arguments = Bundle().apply {
                                putString("searchText", searchText)
                            }
                        }
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.frameLayout, searchFragment)
                            .addToBackStack(null)
                            .commit()
                    }
                    return@setOnEditorActionListener true
                }
                false
            }
        }

        return view
    }

    inner class SliderTask : TimerTask() {
        override fun run() {
            activity?.runOnUiThread {
                if(isAdded) {
                    binding.vpSlider.currentItem = (binding.vpSlider.currentItem.plus(1)).rem(
                        binding.vpSlider.adapter?.itemCount!!
                    )
                }
            }
        }
    }

    private fun setObservers() {
        viewModel.popularMoviesResponse.observe(viewLifecycleOwner) {
            apiCallsFinished++
            if (it != null) {
                Movies = it
                Log.e("setObservers", "$it")
                setUpRv(R.id.rvPopularMovies)
            }
            if (apiCallsFinished == 3) {
                binding?.loadingAnimation?.visibility = View.GONE
                binding?.llContent?.visibility=View.VISIBLE
            }
        }

        viewModel.topRatedMoviesResponse.observe(viewLifecycleOwner) {
            apiCallsFinished++
            if (it != null) {
                Movies = it
                Log.e("setObservers", "$it")
                setUpRv(R.id.rvTopRated)
            }
            if (apiCallsFinished == 3) {
                binding?.loadingAnimation?.visibility = View.GONE
                binding?.llContent?.visibility=View.VISIBLE
            }
        }

        viewModel.upcomingMoviesResponse.observe(viewLifecycleOwner) {
            apiCallsFinished++
            if (it != null) {
                Movies = it
                Log.e("setObservers", "$it")
                setUpRv(R.id.rvUpcoming)
            }
            if (apiCallsFinished == 3) {
                binding?.loadingAnimation?.visibility = View.GONE
                binding?.llContent?.visibility=View.VISIBLE
            }
        }
    }

    fun setUpRv(rvId:Int){
        var popularMoviesList:MutableList<Result> = arrayListOf()

        for(i in 0..Movies?.results?.size!!-1){
            try {
                popularMoviesList.add(Movies?.results!![i])
            }catch (e:Exception){
                Log.e("Error",e.toString())
            }

        }
        val adapterUp=MovieAdapter(requireContext(),popularMoviesList)

        try {
            val rvPopular = view?.findViewById<RecyclerView>(rvId)
            rvPopular?.layoutManager= LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            rvPopular?.adapter=adapterUp
        }catch (e:Exception){
            Log.e("error",e.toString())
        }

        Log.e("Done","Done")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}