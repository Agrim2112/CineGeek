package com.example.cinegeek

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.MoviesViewModel
import com.example.cinegeek.databinding.FragmentExploreBinding
import com.example.cinegeek.databinding.FragmentSearchBinding
import com.example.models.Backdrop
import com.example.models.Movies
import com.example.models.Result
import dagger.hilt.android.AndroidEntryPoint
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
                        .addToBackStack(null)
                        .commit()
                }
                return@setOnEditorActionListener true
            }
            false
        }

        viewModel = ViewModelProvider(this)[MoviesViewModel::class.java]

        if(isAdded)
            binding?.etSearch?.text

        setObservers()
        viewModel.getSearchResults(searchText!!)

        return view
    }

    private fun setObservers() {
        viewModel.searchResultsReponse.observe(viewLifecycleOwner) {
            if (it != null) {
                Movies = it
                Log.e("searchResult", "$it")
                setUpSearchResultsRv()
            }
        }
    }

    private fun setUpSearchResultsRv() {
        var searchResultsList : MutableList<Result> = arrayListOf()

        if(Movies?.results != null) {
            for (i in 0..Movies?.results!!.size - 1) {
                try {
                    searchResultsList.add(Movies?.results!![i])
                } catch (e: Exception) {
                    Log.e("error", e.toString())
                }
            }
        }

        val searchResultAdapter = SearchResultAdapter(requireContext(), searchResultsList)

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