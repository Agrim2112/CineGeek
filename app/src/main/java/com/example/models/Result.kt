package com.example.models

data class Result(
    var adult: Boolean,
    var backdrop_path: String,
    var genre_ids: List<Int>,
    var id: Int,
    val original_language: String,
    val original_title: String,
    val overview: String,
    val popularity: Double,
    val poster_path: String,
    val release_date: String,
    var title: String,
    val video: Boolean,
    val vote_average: Double,
    val vote_count: Int
)