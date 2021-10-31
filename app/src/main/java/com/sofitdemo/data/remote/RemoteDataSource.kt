package com.sofitdemo.data.remote

import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val appService: AppService
) : BaseDataSource() {

    suspend fun getDrinks(value:String) =
        getResult { appService.getDrinks(value) }

    suspend fun getDrinksAlphabets(value:String) =
        getResult { appService.getDrinksAlphabets(value) }

}