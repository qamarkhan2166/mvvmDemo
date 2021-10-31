package com.sofitdemo.data.remote


import com.sofitdemo.data.constant.URLHelper
import com.sofitdemo.data.remote.responce.drinks.DrinksResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AppService {

    @GET(URLHelper.Drinks)
    suspend fun getDrinks(@Query("s") value:String): Response<DrinksResponse>

    @GET(URLHelper.DrinksAlphabets)
    suspend fun getDrinksAlphabets(@Query("f") value:String): Response<DrinksResponse>

}