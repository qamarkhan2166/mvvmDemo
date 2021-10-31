package com.sofitdemo.ui.mainclass

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.sofitdemo.data.local.db.nosql.dao.FavouriteDrinksDb
import com.sofitdemo.data.repos.AppRepository

class MainViewModel @ViewModelInject constructor(
    private var repository: AppRepository,
) : ViewModel() {
    fun searchWord(keyword: String) = repository.searchWord(keyword)
    fun saveButtonState(btnState:String) = repository.saveButtonState(btnState)
    fun getSearchWord() = repository.getSearchWord()
    fun getButtonState() = repository.getButtonState()
    fun getDrinks(value: String) = repository.getDrinks(value)
    fun getDrinksAlphabets(value: String) = repository.getDrinksAlphabets(value)
    fun addDrinks(data: FavouriteDrinksDb) = repository.addDrinks(data)
    fun getFavouriteDrinks() = repository.getFavouriteDrinks()
}
