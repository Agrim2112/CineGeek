package com.example.di

import com.example.api.ApiService
import com.example.api.FCMApi
import com.example.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FCMModule {
    @Provides
    @Singleton
    fun provideRetrofitInstance(): FCMApi =
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FCMApi::class.java)
}