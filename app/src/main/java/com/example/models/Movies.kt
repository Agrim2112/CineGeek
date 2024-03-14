package com.example.models

data class Movies(
    var page: Int,
    var results: List<Result>,
    var total_pages: Int,
    var total_results: Int
)