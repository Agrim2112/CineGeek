package com.example.api

import com.example.models.PopularMovies
import com.example.utils.Constants
import com.example.utils.Constants.POPULAR
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers

interface ApiService {

    @Headers("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIyMGFkZTRmYTczNzA3YjI2YTJkZjJkMDNmZjdkOWUzMSIsInN1YiI6IjY1ZWY1ZGU3ZDQwZDRjMDE2MmVhNWMxNiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.-p5dMwe9ZbOk6Ck2hmJ1ik4B9VN675IU6Qe7kF3aZOc")
    @GET(POPULAR)
    suspend fun getPopularMovies(): Response<PopularMovies>
}