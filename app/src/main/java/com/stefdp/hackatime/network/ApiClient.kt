package com.stefdp.hackatime.network

import com.stefdp.hackatime.network.backendapi.BackendApiService
import com.stefdp.hackatime.network.hackatimeapi.HackatimeApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    // TODO: remember to switch to false for production
    private const val DEBUG = true
    private const val DEBUG_NETWORK = false

    private const val HACKATIME_BASE_URL = "https://hackatime.hackclub.com/api/"

    private const val BACKEND_BASE_URL = "https://hackatime.stefdp.com/api/"
    private const val BACKEND_BASE_URL_DEBUG = "http://10.0.2.2:3000/api/" // for API local testing

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    val hackatimeApi: HackatimeApiService by lazy {
        val builder = Retrofit.Builder()
            .baseUrl(HACKATIME_BASE_URL)

        if (DEBUG_NETWORK) {
            builder.client(okHttpClient)
        }

        builder
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HackatimeApiService::class.java)
    }

    val backendApi: BackendApiService by lazy {
        val builder = Retrofit.Builder()
            .baseUrl(
                if (DEBUG) BACKEND_BASE_URL_DEBUG
                else BACKEND_BASE_URL
            )
//            .client(okHttpClient)

        if (DEBUG_NETWORK) {
            builder.client(okHttpClient)
        }

        builder
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(BackendApiService::class.java)
    }

//    ======== some tests ========
//    fun getHackatimeApiService(baseUrl: String): HackatimeApiService {
//        val formattedUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
//
//        val retrofit = Retrofit.Builder()
//            .baseUrl(formattedUrl)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//
//        return retrofit.create(HackatimeApiService::class.java)
//    }
}