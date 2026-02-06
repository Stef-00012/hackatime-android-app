package com.stefdp.hackatime.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object HackatimeApiClient {
    private const val BASE_URL = "https://hackatime.hackclub.com/api/v1/"

    val hackatimeApi: HackatimeApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(HackatimeApiService::class.java)
    }
}