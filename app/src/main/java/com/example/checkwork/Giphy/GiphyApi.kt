package com.example.checkwork.Giphy
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// Interfaz para consumir la API de Giphy
interface GiphyApi {
    @GET("v1/gifs/random")
    suspend fun getRandomGif(
        @Query("jagy7Dhyz5Ng5lMTb6RzakRsWOHMydO9") apiKey: String,
        @Query("tag") tag: String? = null // Puedes buscar GIFs por tag
    ): GiphyResponse
}

// Clase de respuesta para manejar la estructura del JSON
data class GiphyResponse(
    val data: GifData
)

data class GifData(
    val images: GifImages
)

data class GifImages(
    val original: GifOriginal
)

data class GifOriginal(
    val url: String
)

// Cliente Retrofit
object RetrofitInstance {
    private const val BASE_URL = "https://api.giphy.com/"

    val api: GiphyApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GiphyApi::class.java)
    }
}
