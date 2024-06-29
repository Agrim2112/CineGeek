package com.example.api

import com.example.models.MovieCast
import com.example.models.MovieDetails
import com.example.models.MovieImages
import com.example.models.Movies
import com.example.utils.Constants.POPULAR
import com.example.utils.Constants.TOP_RATED
import com.example.utils.Constants.UPCOMING
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @Headers("Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIyMGFkZTRmYTczNzA3YjI2YTJkZjJkMDNmZjdkOWUzMSIsInN1YiI6IjY1ZWY1ZGU3ZDQwZDRjMDE2MmVhNWMxNiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.-p5dMwe9ZbOk6Ck2hmJ1ik4B9VN675IU6Qe7kF3aZOc")
    @GET(POPULAR)
    suspend fun getPopularMovies(): Response<Movies>

    @Headers("Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIyMGFkZTRmYTczNzA3YjI2YTJkZjJkMDNmZjdkOWUzMSIsInN1YiI6IjY1ZWY1ZGU3ZDQwZDRjMDE2MmVhNWMxNiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.-p5dMwe9ZbOk6Ck2hmJ1ik4B9VN675IU6Qe7kF3aZOc")
    @GET(TOP_RATED)
    suspend fun getTopRatedMovies(): Response<Movies>

    @Headers("Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIyMGFkZTRmYTczNzA3YjI2YTJkZjJkMDNmZjdkOWUzMSIsInN1YiI6IjY1ZWY1ZGU3ZDQwZDRjMDE2MmVhNWMxNiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.-p5dMwe9ZbOk6Ck2hmJ1ik4B9VN675IU6Qe7kF3aZOc")
    @GET(UPCOMING)
    suspend fun getUpcomingMovies(): Response<Movies>

    @Headers("Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIyMGFkZTRmYTczNzA3YjI2YTJkZjJkMDNmZjdkOWUzMSIsInN1YiI6IjY1ZWY1ZGU3ZDQwZDRjMDE2MmVhNWMxNiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.-p5dMwe9ZbOk6Ck2hmJ1ik4B9VN675IU6Qe7kF3aZOc")
    @GET("https://api.themoviedb.org/3/movie/{id}")
    suspend fun getMovieDetails(@Path("id") id: String): Response<MovieDetails>

    @Headers("Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIyMGFkZTRmYTczNzA3YjI2YTJkZjJkMDNmZjdkOWUzMSIsInN1YiI6IjY1ZWY1ZGU3ZDQwZDRjMDE2MmVhNWMxNiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.-p5dMwe9ZbOk6Ck2hmJ1ik4B9VN675IU6Qe7kF3aZOc")
    @GET("https://api.themoviedb.org/3/movie/{id}/similar")
    suspend fun getSimilarMovies(@Path("id") id: String): Response<Movies>

    @Headers("Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIyMGFkZTRmYTczNzA3YjI2YTJkZjJkMDNmZjdkOWUzMSIsInN1YiI6IjY1ZWY1ZGU3ZDQwZDRjMDE2MmVhNWMxNiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.-p5dMwe9ZbOk6Ck2hmJ1ik4B9VN675IU6Qe7kF3aZOc")
    @GET("https://api.themoviedb.org/3/movie/{id}/credits")
    suspend fun getMovieCast (@Path("id") id:String) : Response<MovieCast>

    @Headers("Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIyMGFkZTRmYTczNzA3YjI2YTJkZjJkMDNmZjdkOWUzMSIsInN1YiI6IjY1ZWY1ZGU3ZDQwZDRjMDE2MmVhNWMxNiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.-p5dMwe9ZbOk6Ck2hmJ1ik4B9VN675IU6Qe7kF3aZOc")
    @GET("https://api.themoviedb.org/3/movie/{id}/images")
    suspend fun getMovieImages(@Path("id") id:String): Response<MovieImages>

    @Headers("Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIyMGFkZTRmYTczNzA3YjI2YTJkZjJkMDNmZjdkOWUzMSIsInN1YiI6IjY1ZWY1ZGU3ZDQwZDRjMDE2MmVhNWMxNiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.-p5dMwe9ZbOk6Ck2hmJ1ik4B9VN675IU6Qe7kF3aZOc")
    @GET("https://api.themoviedb.org/3/search/movie")
    suspend fun getSearchResults(@Query("query") search: String
    ):Response<Movies>
}

