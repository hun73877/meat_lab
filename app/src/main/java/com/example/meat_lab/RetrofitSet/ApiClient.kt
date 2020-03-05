package com.example.meat_lab

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient
{
    val BASE_URL = "http://115.68.231.84/" // iwinv

    private var retrofit: Retrofit? = null

    val apiClient: Retrofit?
        get()
        {
            if (retrofit == null)
            {
                retrofit =
                    Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build()
            }
            return retrofit
        }
}