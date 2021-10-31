package com.sofitdemo.ui.splash

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
//import store.nextfitting.data.remote.socket.AppSocket
import com.sofitdemo.data.repos.AppRepository

class SplashViewModel @ViewModelInject constructor(
   private var repository: AppRepository,
    //val socket: AppSocket
) : ViewModel() {
    var isLogin = repository.isLogin()
    //fun getData() = repository.addData()

}
