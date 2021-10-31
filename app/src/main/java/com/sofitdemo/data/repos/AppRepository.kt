package com.sofitdemo.data.repos

import com.google.gson.Gson
import com.sofitdemo.data.local.db.nosql.dao.FavouriteDrinksDb
import com.sofitdemo.data.local.prefs.AppPrefs
import com.sofitdemo.data.remote.RemoteDataSource
import com.sofitdemo.utils.NetworkOnly
import io.realm.Realm
import io.realm.kotlin.where
import javax.inject.Inject

class AppRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
//    private val localDataSource: LocalDataSource,
    private val prefs: AppPrefs,
    private val gson: Gson,
    private val realm: Realm
) {
    fun isLogin() = prefs.isLogin
    fun searchWord(keyword:String) = prefs.saveSearchWord(keyword)
    fun saveButtonState(btnState:String) = prefs.saveButtonState(btnState)
    fun getSearchWord() = prefs.getSearchWord()
    fun getButtonState() = prefs.getButtonState()



    fun getDrinks(value:String) = NetworkOnly(
        networkCall = { remoteDataSource.getDrinks(value) }
    )
    fun getDrinksAlphabets(value:String) = NetworkOnly(
        networkCall = { remoteDataSource.getDrinksAlphabets(value) }
    )

    fun getFavouriteDrinks(): List<FavouriteDrinksDb>? {
        var items:List<FavouriteDrinksDb>? = null
        realm.executeTransaction { realm ->
            val data = realm.where<FavouriteDrinksDb>().findAll()
            items = data
        }
        return items
    }

    fun addDrinks(data:FavouriteDrinksDb){
        realm.executeTransaction {
            it.insertOrUpdate(data)
        }
    }
}