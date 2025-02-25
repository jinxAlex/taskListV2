package com.example.tasklist.model

import com.google.gson.annotations.SerializedName

data class Imagen(
    @SerializedName("urls") val urls: Urls
)

data class Urls(
    @SerializedName("small") val small: String
)

data class ListDatosImagen(
    @SerializedName("results") val results: List<Imagen>
)
