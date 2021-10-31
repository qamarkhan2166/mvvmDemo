package com.sofitdemo.data.local.prefs

import android.content.SharedPreferences

import com.sofitdemo.data.local.prefs.util.SharedPreferenceLiveData
import javax.inject.Inject

class AppPrefs @Inject constructor(private val pref: SharedPreferences?) {
    private var IS_LOGIN = "is_login"
    private var TOKEN_ = "user_token"
    private var REFRESH_TOKEN = "refresh_token"
    private var DEVICE_TOKEN = "device_token"
    private var DEVICE_ID = "device_id"
    private var SEARCH_STATE = "search_state"
    private var SAVE_BUTTON_STATE = "save_button_state"

    var isLogin: Boolean
        get() = pref!!.getBoolean(IS_LOGIN, false)
        set(value) {
            pref?.edit()?.putBoolean(IS_LOGIN, value)?.apply()
        }
    var TOKEN: String
        get() = pref!!.getString(TOKEN_, "")!!
        set(value) {
            pref?.edit()?.putString(TOKEN_, value)?.apply()
        }
    var refreshToken: String
        get() = pref!!.getString(REFRESH_TOKEN, "")!!
        set(value) {
            pref?.edit()?.putString(REFRESH_TOKEN, value)?.apply()
        }

    var deviceToken: String
        get() = pref!!.getString(DEVICE_TOKEN, "")!!
        set(value) {
            pref?.edit()?.putString(DEVICE_TOKEN, value)?.apply()
        }

    var deviceId: String
        get() = pref!!.getString(DEVICE_ID, "")!!
        set(value) {
            pref?.edit()?.putString(DEVICE_ID, value)?.apply()
        }

  /*  var searchWord: String
        get() = pref?.getString(SEARCH_STATE, "margarita")!!
        set(value) {
            pref?.edit()?.putString(SEARCH_STATE, value)?.apply()
        }*/

    fun saveSearchWord (keyWord:String){
        pref?.edit()?.putString(SEARCH_STATE,keyWord)?.apply()
    }
    fun getSearchWord ():String{

        return  pref!!.getString(SEARCH_STATE, "margarita")!!
    }

    fun saveButtonState (keyWord:String){
        pref?.edit()?.putString(SAVE_BUTTON_STATE,keyWord)?.apply()
    }
    fun getButtonState ():String{
        return  pref!!.getString(SAVE_BUTTON_STATE, "1")!!
    }



    inline fun <reified T> SharedPreferences.liveData(
        key: String,
        default: T
    ): SharedPreferenceLiveData<T> {
        @Suppress("UNCHECKED_CAST")
        return object : SharedPreferenceLiveData<T>(this, key, default) {
            override fun getValueFromPreferences(key: String, defValue: T): T {
                return when (default) {
                    is String -> getString(key, default) as T
                    is Int -> getInt(key, default) as T
                    is Long -> getLong(key, default) as T
                    is Boolean -> getBoolean(key, default) as T
                    is Float -> getFloat(key, default) as T
                    is Set<*> -> getStringSet(key, default as Set<String>) as T
                    is MutableSet<*> -> getStringSet(key, default as MutableSet<String>) as T
                    else -> throw IllegalArgumentException("generic type not handled")
                }
            }
        }
    }
}