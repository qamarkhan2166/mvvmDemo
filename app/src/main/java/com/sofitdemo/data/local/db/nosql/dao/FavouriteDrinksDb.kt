package com.sofitdemo.data.local.db.nosql.dao

import io.realm.RealmObject

open class FavouriteDrinksDb : RealmObject() {

    @io.realm.annotations.PrimaryKey
    var idDrink: Int? = null
    var strDrink: String? = null
    var strCategory: String? = null
    var strInstructions: String? = null
    var strDrinkThumb: String? = null
    var strAlcoholic: String? = null
}