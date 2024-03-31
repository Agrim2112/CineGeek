package com.example

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.models.MovieCast
import com.example.models.MovieDetails
import com.example.models.Movies
import com.example.repository.MoviesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel
@Inject constructor(private val moviesRepository: MoviesRepository): ViewModel()
{
    private val fetchPopularMovies=MutableLiveData<Movies>()
    val popularMoviesResponse:LiveData<Movies>
        get()=fetchPopularMovies

    private val fetchTopRatedMovies=MutableLiveData<Movies>()
    val topRatedMoviesResponse:LiveData<Movies>
        get()=fetchTopRatedMovies

    private val fetchUpcomingMovies=MutableLiveData<Movies>()
    val upcomingMoviesResponse:LiveData<Movies>
        get()=fetchUpcomingMovies

    private val fetchMovieDetails=MutableLiveData<MovieDetails>()
    val movieDetailsResponse:LiveData<MovieDetails>
        get()=fetchMovieDetails

    private val fetchSimilarMoviesList=MutableLiveData<Movies>()

    val similarMovieListResponse:LiveData<Movies>
        get() = fetchSimilarMoviesList

    private val fetchMovieCast=MutableLiveData<MovieCast>()
    val movieCastResponse:LiveData<MovieCast>
        get() = fetchMovieCast

    init {
        getPopularMovies()
        getTopRatedMovies()
        getUpcomingMovies()
    }

    private fun getPopularMovies(){
        viewModelScope.launch {
            moviesRepository.getPopularMovies().let {response->
                if(response.isSuccessful){
                    fetchPopularMovies.postValue(response.body())
                }
                else{
                    Log.d("MoviesViewModel", "getPopularMovies: ${response.errorBody()}")
                }
            }
    }
    }

    private fun getTopRatedMovies(){
        viewModelScope.launch {
            moviesRepository.getTopRatedMovies().let {response->
                if(response.isSuccessful){
                    fetchTopRatedMovies.postValue(response.body())
                }
                else{
                    Log.d("MoviesViewModel", "getTopRatedMovies: ${response.errorBody()}")
                }
            }
        }
    }

    private fun getUpcomingMovies(){
        viewModelScope.launch {
            moviesRepository.getUpcomingMovies().let {response->
                if(response.isSuccessful){
                    fetchUpcomingMovies.postValue(response.body())
                }
                else{
                    Log.d("MoviesViewModel", "getUpcomingMovies: ${response.errorBody()}")
                }
            }
        }
    }

    fun getMovieDetails(movieId: String) {
        viewModelScope.launch {
            moviesRepository.getMovieDetails(movieId).let { response ->
                if(response.isSuccessful){
                    fetchMovieDetails.postValue(response.body())
                }
                else{
                    Log.d("MoviesViewModel","getMovieDetails: ${response.errorBody()}")
                }
            }
        }
    }

    fun getSimilarMovies(movieId:String){
        viewModelScope.launch {
            moviesRepository.getSimilarMovies(movieId).let { response ->
                if (response.isSuccessful){
                    fetchSimilarMoviesList.postValue(response.body())
                }
                else{
                    Log.d("MoviesViewModel","getSimilarMovies:${response.errorBody()}")
                }
            }
        }
    }

    fun getMovieCast(movieId:String){
        viewModelScope.launch {
            moviesRepository.getMovieCast(movieId).let { response ->
                if (response.isSuccessful){
                    fetchMovieCast.postValue(response.body())
                }
                else{
                    Log.d("MoviesViewModel","getMovieCast:${response.errorBody()}")
                }
            }
        }
    }

}