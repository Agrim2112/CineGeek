package com.example

import android.util.Log
import android.widget.MultiAutoCompleteTextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.models.FavouriteModel
import com.example.models.MovieCast
import com.example.models.MovieDetails
import com.example.models.MovieImages
import com.example.models.Movies
import com.example.repository.MoviesRepository
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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

    private val fetchFavourites=MutableLiveData<List<FavouriteModel>>()
    val fetchFavouritesResponse:LiveData<List<FavouriteModel>>
        get()=fetchFavourites

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

    fun addToFavourites(favouriteDetails:FavouriteModel) {
        myRef.child(favouriteDetails.movieId).setValue(favouriteDetails)
            .addOnCompleteListener {
                Log.e("MoviesViewModel", "addToFavourites: ${it.isSuccessful}")
            }
            .addOnFailureListener {
                Log.e("MoviesViewModel", "addToFavourites: ${it.message}")
            }
    }

    fun removeFromFavourites(favouriteDetails: FavouriteModel) {
//        viewModelScope.launch {
//            myRef.addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    for (data in snapshot.children) {
//                        val fav = data.getValue(FavouriteModel::class.java)
//                        if (fav != null) {
//                            if (fav.movieId == favouriteDetails.movieId) {
//                                myRef.child(data.key!!).removeValue()
//                                    .addOnCompleteListener {
//                                        Log.e("MoviesViewModel", "removeFromFavourites: ${it.isSuccessful}")
//                                    }
//                                    .addOnFailureListener {
//                                        Log.e("MoviesViewModel", "removeFromFavourites: ${it.message}")
//                                    }
//                            }
//                        }
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    Log.e("MoviesViewModel", "onCancelled: ${error.message}")
//                }
//            })
//        }

        myRef.child(favouriteDetails.movieId).removeValue()
            .addOnCompleteListener {
                Log.e("MoviesViewModel", "removeFromFavourites: ${it.isSuccessful}")
            }
            .addOnFailureListener {
                Log.e("MoviesViewModel", "removeFromFavourites: ${it.message}")
            }
    }

    fun getFavourites(){
        viewModelScope.launch {
            myRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<FavouriteModel>()
                    for (data in snapshot.children) {
                        val fav = data.getValue(FavouriteModel::class.java)
                        if (fav != null) {
                            list.add(fav)
                        }
                    }
                    Log.e("MoviesViewModel", "onDataChange: $list")
                    fetchFavourites.postValue(list)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MoviesViewModel", "onCancelled: ${error.message}")
                }
            })
        }
    }

    fun checkFavourites(movieId:String){
        viewModelScope.launch {
            myRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    isFavourite.postValue(false)
                    for (data in snapshot.children) {
                        val fav = data.getValue(FavouriteModel::class.java)
                        Log.e("MoviesViewModel", "onDataChange: $fav")
                        if (fav != null) {
                            if (fav.movieId == movieId) {
                                Log.e("MoviesViewModel", "onDataChange: ${fav.movieId}")
                                isFavourite.postValue(true)
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("MoviesViewModel", "onCancelled: ${error.message}")
                }
            })
        }
    }

}