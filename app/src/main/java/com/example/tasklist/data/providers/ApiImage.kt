package com.example.tasklist.data.providers

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiImage {
    private val retrofit2 = Retrofit.Builder().baseUrl("https://api.unsplash.com/").addConverterFactory(GsonConverterFactory.create()).build()

    val api = retrofit2.create(ImageInterface::class.java)
}