package com.sofitdemo.utils.network

import java.io.IOException

class NetworkConnectivityError : IOException() {
     override val message: String
        get() = "No Internet Connection"
}