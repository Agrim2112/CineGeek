package com.example.repository

import com.example.api.ApiService
import javax.inject.Inject

class MoviesRepository
@Inject constructor(private val apiService: ApiService){

    suspend fun getPopularMovies() = apiService.getPopularMovies()
}