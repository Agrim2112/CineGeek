package com.example.repository

import com.example.api.ApiService
import javax.inject.Inject

class MoviesRepository
@Inject constructor(private val apiService: ApiService){

    suspend fun getPopularMovies() = apiService.getPopularMovies()

    suspend fun getTopRatedMovies() = apiService.getTopRatedMovies()

    suspend fun getUpcomingMovies() = apiService.getUpcomingMovies()
    suspend fun getMovieDetails(id: String) = apiService.getMovieDetails(id)

    suspend fun getSimilarMovies(id:String) = apiService.getSimilarMovies(id)
    suspend fun getMovieCast (id:String) = apiService.getMovieCast(id)
}