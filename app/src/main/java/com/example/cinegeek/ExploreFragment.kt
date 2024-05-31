package com.example.cinegeek

import SliderAdapter
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.MoviesViewModel
import com.example.cinegeek.databinding.FragmentExploreBinding
import com.example.di.Resource
import com.example.models.Movies
import com.example.models.Result
import com.example.models.Slider
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
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

        binding?.etSearch?.setOnTouchListener { v, event ->
            val DRAWABLE_RIGHT = 2

            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (binding.etSearch.right - binding.etSearch.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {
                    askSpeechInput()
                    Log.d("speech","Working")
                    v.performClick()
                    return@setOnTouchListener true
                }
            }
            false
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 100){
            if(resultCode ==  Activity.RESULT_OK && data != null){
                val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                binding.etSearch.setText(result?.get(0))
                Log.d("speech","Working")

                if(result?.get(0)?.isNotEmpty() == true){
                    Log.d("speech","Working")
                    val searchFragment = SearchFragment().apply {
                        arguments = Bundle().apply {
                            putString("searchText", result?.get(0))
                        }
                    }
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, searchFragment)
                        .addToBackStack("ExploreFragment")
                        .commit()
                }
            }
        }
    }
    fun askSpeechInput() {
        Log.d("speech","Working")
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to search")
        startActivityForResult(intent, 100)
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
            when(it.status){
                Resource.Status.LOADING -> {
                    Log.d("setObservers", "Loading")
                    binding?.loadingAnimation?.visibility = View.VISIBLE
                    binding?.llContent?.visibility=View.GONE
                }
                Resource.Status.EMPTY -> {
                    Log.d("setObservers", "Empty")
                    binding?.loadingAnimation?.visibility = View.VISIBLE
                    binding?.llContent?.visibility=View.GONE
                }
                Resource.Status.SUCCESS -> {
                    Log.d("setObservers", "Success")
                    binding?.loadingAnimation?.visibility = View.GONE
                    binding?.llContent?.visibility=View.VISIBLE
                    Movies = it.data
                    Log.e("setObservers", "${it.data}")
                    setUpRv(R.id.rvPopularMovies)
                }
                Resource.Status.ERROR -> {
                    Log.e("setObservers", it.error.toString())
                    Toast.makeText(requireContext(),it.error.toString(),Toast.LENGTH_SHORT).show()
                }
                else -> {}
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