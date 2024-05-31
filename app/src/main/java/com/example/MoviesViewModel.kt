package com.example

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.di.Resource
import com.example.models.UserFavouriteModel
import com.example.models.MovieCast
import com.example.models.MovieDetails
import com.example.models.MovieFavourites
import com.example.models.MovieImages
import com.example.models.Movies
import com.example.repository.MoviesRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.net.UnknownHostException
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel
@Inject constructor(private val moviesRepository: MoviesRepository): ViewModel()
{
    val database = FirebaseDatabase.getInstance()
    val myRef = database.getReference("favourites")
    private val isFavourite=MutableLiveData<Boolean>()
    val isFavouriteResponse:LiveData<Boolean>
        get()=isFavourite

    private val fetchFavourites=MutableLiveData<List<String>>()
    val fetchFavouritesResponse:LiveData<List<String>>
        get()=fetchFavourites

    private val fetchPopularMovies=MutableLiveData<Resource<Movies>>()
    val popularMoviesResponse:LiveData<Resource<Movies>>
        get()=fetchPopularMovies

    private val fetchTopRatedMovies=MutableLiveData<Resource<Movies>>()
    val topRatedMoviesResponse:LiveData<Resource<Movies>>
        get()=fetchTopRatedMovies

    private val fetchUpcomingMovies=MutableLiveData<Resource<Movies>>()
    val upcomingMoviesResponse:LiveData<Resource<Movies>>
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

    private val fetchMovieImages = MutableLiveData<MovieImages>()
    val movieImagesResponse: LiveData<MovieImages>
        get() = fetchMovieImages

    private val fetchSearchResults = MutableLiveData<Movies>()

    val searchResultsReponse: LiveData<Movies>
        get() = fetchSearchResults

    init {
        getPopularMovies()
        getTopRatedMovies()
        getUpcomingMovies()

    }

    private fun getPopularMovies(){
        viewModelScope.launch {
            try {
                fetchPopularMovies.value=Resource.loading()
                val response = moviesRepository.getPopularMovies()
                if (response.isSuccessful) {
                    fetchPopularMovies.value = Resource.success(response.body()!!)
                } else {
                    fetchPopularMovies.value = Resource.empty()
                }
            }
            catch (e:Exception){
                if (e is UnknownHostException) {
                    fetchPopularMovies.value = Resource.offlineError()
                } else {
                    fetchPopularMovies.value = Resource.error(e)
                }
            }
    }
    }

    private fun getTopRatedMovies() {
        viewModelScope.launch {
            try {
                fetchTopRatedMovies.value = Resource.loading()
                val response = moviesRepository.getTopRatedMovies()
                if (response.isSuccessful) {
                    fetchTopRatedMovies.value = Resource.success(response.body()!!)
                } else {
                    fetchTopRatedMovies.value = Resource.empty()
                }
            } catch (e: Exception) {
                if (e is UnknownHostException) {
                    fetchTopRatedMovies.value = Resource.offlineError()
                } else {
                    fetchTopRatedMovies.value = Resource.error(e)
                }
            }
        }
    }

    private fun getUpcomingMovies(){
        viewModelScope.launch {
            try {
                fetchUpcomingMovies.value = Resource.loading()
                val response = moviesRepository.getUpcomingMovies()
                if (response.isSuccessful) {
                    fetchUpcomingMovies.value = Resource.success(response.body()!!)
                } else {
                    fetchUpcomingMovies.value = Resource.empty()
                }
            } catch (e: Exception) {
                if (e is UnknownHostException) {
                    fetchUpcomingMovies.value = Resource.offlineError()
                } else {
                    fetchUpcomingMovies.value = Resource.error(e)
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

    fun getMovieImages(movieId: String){
        viewModelScope.launch {
            moviesRepository.getMovieImages(movieId).let {response ->
                if(response.isSuccessful)
                {
                    fetchMovieImages.postValue(response.body())
                }
                else{
                    Log.d("MoviesViewModel","getMovieImages:${response.errorBody()}")
                }
            }
        }
    }

    fun getSearchResults(search:String){
        viewModelScope.launch {
            moviesRepository.getSearchResults(search).let { response ->
                if (response.isSuccessful){
                    fetchSearchResults.postValue(response.body())
                }
                else{
                    Log.d("MoviesViewModel","getSearchResults;${response.errorBody()}")
                }
            }
        }
    }

    fun addToFavourites(userID:String,movieId: String) {
        viewModelScope.launch {
            val userFav = database.getReference("UserFavourites").child(userID)
            val movieFav = database.getReference("MovieFavourites").child(movieId)

            userFav.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val currentMovies =
                        snapshot.getValue(UserFavouriteModel::class.java)?.movieId ?: listOf()
                    if (!currentMovies.contains(movieId)) {
                        val updatedMovies = currentMovies + movieId
                        userFav.setValue(UserFavouriteModel(updatedMovies))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MoviesViewModel", "onCancelled: ${error.message}")
                }

            })

            movieFav.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val currentUsers =
                        snapshot.getValue(MovieFavourites::class.java)?.userId ?: listOf()
                    if(!currentUsers.contains(userID)) {
                        val updatedUsers = currentUsers + userID
                        movieFav.setValue(MovieFavourites(updatedUsers))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MoviesViewModel", "onCancelled: ${error.message}")
                }

            })
        }
    }

    fun removeFromFavourites(userID:String,movieId: String) {
        viewModelScope.launch {
            val userFav = database.getReference("UserFavourites").child(userID)
            val movieFav = database.getReference("MovieFavourites").child(movieId)

            userFav.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val currentMovies =
                        snapshot.getValue(UserFavouriteModel::class.java)?.movieId ?: listOf()
                    val updatedMovies = currentMovies.filter { it != movieId }
                    userFav.setValue(UserFavouriteModel(updatedMovies))
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MoviesViewModel", "onCancelled: ${error.message}")
                }

            })

            movieFav.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val currentUsers =
                        snapshot.getValue(MovieFavourites::class.java)?.userId ?: listOf()
                    val updatedUsers = currentUsers.filter { it != userID }
                    movieFav.setValue(MovieFavourites(updatedUsers))
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MoviesViewModel", "onCancelled: ${error.message}")
                }

            })
        }
    }

    fun getFavourites(){
        viewModelScope.launch {
            val userFav = database.getReference("UserFavourites").child(FirebaseAuth.getInstance().uid!!)
            userFav.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userFavouriteModel = snapshot.getValue(UserFavouriteModel::class.java)
                    val movieIds = userFavouriteModel?.movieId ?: listOf()
                    fetchFavourites.postValue(movieIds)
                    }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MoviesViewModel", "onCancelled: ${error.message}")
                }
            })
        }
    }

    fun checkFavourites(movieId:String){
        viewModelScope.launch {
            val movieFav = database.getReference("UserFavourites").child(FirebaseAuth.getInstance().uid!!)

            movieFav.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val userFavouriteModel = snapshot.getValue(UserFavouriteModel::class.java)
                    val movieIds = userFavouriteModel?.movieId ?: listOf()
                    isFavourite.postValue(movieIds.contains(movieId))
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MoviesViewModel", "onCancelled: ${error.message}")
                }
            })
        }
    }

}