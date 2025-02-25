package com.example.tasklist.data.providers

import com.example.tasklist.model.ListDatosImagen
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ImageInterface {
    @GET("search/photos")

    suspend fun getImages(@Query("query") query: String,@Query("client_id") client_id: String,): Response<ListDatosImagen>
}