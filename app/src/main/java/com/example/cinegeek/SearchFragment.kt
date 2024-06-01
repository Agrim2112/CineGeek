package com.example.cinegeek

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.MoviesViewModel
import com.example.cinegeek.databinding.FragmentExploreBinding
import com.example.cinegeek.databinding.FragmentSearchBinding
import com.example.models.Backdrop
import com.example.models.MovieDetails
import com.example.models.Movies
import com.example.models.Result
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Integer.min
import java.util.Locale
import java.util.Timer


@AndroidEntryPoint
class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    lateinit var viewModel : MoviesViewModel
    private var Movies : Movies?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val view = binding.root

        val searchText = arguments?.getString("searchText")

        binding?.etSearch?.setOnEditorActionListener{v,actionId,event->
            if(actionId== EditorInfo.IME_ACTION_SEARCH){
                val searchText = binding.etSearch.text.toString()
                if(searchText.isNotEmpty()){
                    val searchFragment = SearchFragment().apply {
                        arguments = Bundle().apply {
                            putString("searchText", searchText)
                        }
                    }
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, searchFragment)
                        .addToBackStack("ExploreFragment")
                        .commit()
                }
                return@setOnEditorActionListener true
            }
            false
        }

        viewModel = ViewModelProvider(this)[MoviesViewModel::class.java]

        if(isAdded) {
            val factory = Editable.Factory.getInstance()
            binding?.etSearch?.text = factory.newEditable(searchText)
        }

        binding?.etSearch?.setOnTouchListener { v, event ->
            val DRAWABLE_LEFT = 0
            val DRAWABLE_RIGHT = 2

            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (binding.etSearch.right - binding.etSearch.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {
                    askSpeechInput()
                    Log.d("speech","Working")
                    v.performClick()
                    return@setOnTouchListener true
                } else if (event.rawX <= (binding.etSearch.left + binding.etSearch.compoundDrawables[DRAWABLE_LEFT].bounds.width())) {
                    parentFragmentManager.popBackStack()
                    return@setOnTouchListener true
                }
            }
            false
        }

        setObservers()
        viewModel.getSearchResults(searchText!!)

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

    private fun setObservers() {
        val movieDetailsList = mutableListOf<MovieDetails>()
        viewModel.searchResultsReponse.observe(viewLifecycleOwner) {
            if (it != null) {
                Movies = it
                Log.e("searchResult", "$it")

                if(Movies?.results?.isEmpty()==true)
                {
                    binding?.rvSearchResults?.visibility = View.GONE
                    binding?.tvNA?.visibility = View.VISIBLE
                }

                for (i in 0..min(14,Movies?.results!!.size-1)) {
                    viewModel.getMovieDetails(Movies?.results!![i]?.id.toString()!!)
                }
            }
        }

        viewModel.movieDetailsResponse.observe(viewLifecycleOwner) {
            if (it != null) {
                movieDetailsList.add(it)
            }
            if(movieDetailsList.size>0 && movieDetailsList.size == min(14,Movies?.results!!.size)) {
                binding?.llContent?.visibility=View.VISIBLE
                binding?.loadingAnimation?.visibility=View.GONE
                Log.e("movieDetailsList", "$movieDetailsList")
                setUpSearchResultsRv(movieDetailsList)
            }
        }
    }

    private fun setUpSearchResultsRv(movieDetailsList: List<MovieDetails>) {
        val searchResultAdapter = FavouritesListAdapter(requireContext(), movieDetailsList as MutableList<MovieDetails>)

        try {
            binding?.rvSearchResults?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            binding?.rvSearchResults?.adapter = searchResultAdapter
        } catch (e:Exception){
            Log.e("Error", e.toString())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}