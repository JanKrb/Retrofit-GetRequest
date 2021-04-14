package com.jankrb.retrofit_getrequest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.*
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val service = RetrofitFactory.makeRetrofitService() // Retrofit service
        CoroutineScope(Dispatchers.IO).launch { // Coroutine to load sync
            val response = service.getTodos() // Response coming from api
            withContext(Dispatchers.Main) {
                try {
                    if (response.isSuccessful) { // If Response is HTTP 200 / OK
                        Log.i("SUC", "Successfully loaded")
                        Log.i("SUC", response.body().toString())
                    } else { // If Response is failed for any reason
                        Log.e("ERR", "Failed to fetch data")
                    }
                }
                catch (e: HttpException) {}
                catch (e: Throwable) {}
            }
        }
    }

    /**
     * Class for Retrofit service response
     */
    class Todo {
        var userId: Int? = null
        var id: Int? = null
        var title: String? = null
        var completed: Boolean? = null
    }

    /**
     * Service to register Response name and type
     */
    interface RetrofitService {
        @GET("/todos") // Type and route of api call
        suspend fun getTodos(): Response<List<Todo>> // Function to fetch data, register Response type
    }

    /**
     * Factory to create requests
     */
    object RetrofitFactory {
        private const val BASE_URL = "https://jsonplaceholder.typicode.com" // URL of the api (Without route or tail slash)

        fun makeRetrofitService(): RetrofitService { // Function to create Retrofit
            return Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build().create(RetrofitService::class.java)
        }
    }
}