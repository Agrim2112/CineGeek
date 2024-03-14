package com.example

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.models.Movies
import com.example.repository.MoviesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel
@Inject constructor(private val moviesRepository: MoviesRepository): ViewModel()
{
    private val res=MutableLiveData<Movies>()
    val popularMoviesResponse:LiveData<Movies>
        get()=res

    init {
        getPopularMovies()
    }

    private fun getPopularMovies(){
        viewModelScope.launch {
            moviesRepository.getPopularMovies().let {response->
                if(response.isSuccessful){
                    res.postValue(response.body())
                }
                else{
                    Log.d("MoviesViewModel", "getPopularMovies: ${response.errorBody()}")
                }
            }
    }
    }

}