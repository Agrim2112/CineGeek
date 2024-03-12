package com.example

import com.example.repository.MoviesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel
@Inject constructor(private val moviesRepository: MoviesRepository){

}