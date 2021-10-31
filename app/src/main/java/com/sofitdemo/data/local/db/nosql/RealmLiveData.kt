package com.sofitdemo.data.local.db.nosql

//class RealmLiveData<T : RealmModel?>(private val results: RealmResults<T>) :
//    LiveData<RealmResults<T>?>() {
//    private val listener: RealmChangeListener<RealmResults<T>> =
//        RealmChangeListener { results -> value = results }
//
//    override fun onActive() {
//        results.addChangeListener(listener)
//    }
//
//    override fun onInactive() {
//        results.removeChangeListener(listener)
//    }
//}