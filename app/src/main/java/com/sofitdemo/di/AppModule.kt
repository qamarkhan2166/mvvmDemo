package com.sofitdemo.di

//import store.nextfitting.data.remote.socket.AppSocket
//import org.komamitsu.retrofit.converter.msgpack.MessagePackConverterFactory
import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.sofitdemo.BuildConfig
import com.sofitdemo.OnAuthFailed
import com.sofitdemo.data.constant.Preferences.Companion.APP_NAME
import com.sofitdemo.data.constant.URLHelper.base
import com.sofitdemo.data.local.prefs.AppPrefs
import com.sofitdemo.data.remote.AppService
import com.sofitdemo.data.remote.RemoteDataSource
import com.sofitdemo.data.repos.AppRepository
import com.sofitdemo.utils.CheckInternetConnection
import com.sofitdemo.utils.network.NetworkConnectivityError
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.realm.Realm
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.jetbrains.annotations.NotNull
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val TIMEOUT_IN_MS = 30000
    private const val LT_BASE_URL = "lt_base_url"
    private const val TOKEN = "token"
    private const val REFRESH_TOKEN = "refresh_token"

    private val BASE_URL = "https://api.github.com/"
    private lateinit var appRepository: AppRepository

    @Provides
    @Singleton
    fun provideGsonConverterFactory(gson: Gson): GsonConverterFactory =
        GsonConverterFactory.create(gson)

    @Singleton
    @Provides
    fun provideRetrofit(okhttpClient: OkHttpClient, gson: Gson): Retrofit = Retrofit.Builder()
        .baseUrl(base)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(okhttpClient)
        .build()

    @Provides
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    fun provideAppService(retrofit: Retrofit): AppService =
        retrofit.create(AppService::class.java)

    @Singleton
    @Provides
    fun providesHttpLoggingInterceptor() = HttpLoggingInterceptor().apply {
        level =
            if (!BuildConfig.DEBUG) HttpLoggingInterceptor.Level.NONE else HttpLoggingInterceptor.Level.BODY
    }

    @Singleton
    @Provides
    fun provideRemoteDataSource(characterService: AppService) =
        RemoteDataSource(characterService)

    @Singleton
    @Provides
    fun provideRepository(
        remoteDataSource: RemoteDataSource,
//        localDataSource: LocalDataSource,
        prefs: AppPrefs,
        realm: Realm,
        gson: Gson,
    ): AppRepository {
        appRepository = AppRepository(
            remoteDataSource,
//            localDataSource,
            prefs,
            gson, realm
        )
        return appRepository
    }


    @Singleton
    @Provides
    fun provideOKHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: Interceptor,
        authenticator: Authenticator,
        @ApplicationContext context: Context
    ): OkHttpClient {        // Create a trust manager that does not validate certificate chains
        val trustAllCerts =
            arrayOf<TrustManager>(
                object : X509TrustManager {
                    @Throws(CertificateException::class)
                    override fun checkClientTrusted(
                        chain: Array<X509Certificate>,
                        authType: String
                    ) {
                    }

                    @Throws(CertificateException::class)
                    override fun checkServerTrusted(
                        chain: Array<X509Certificate>,
                        authType: String
                    ) {
                    }

                    override fun getAcceptedIssuers(): Array<X509Certificate> {
                        return arrayOf()
                    }
                }
            )

        // Install the all-trusting trust manager

        // Install the all-trusting trust manager
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())
        // Create an ssl socket factory with our all-trusting manager
        val sslSocketFactory = sslContext.socketFactory

        val builder = OkHttpClient.Builder()
        builder.sslSocketFactory(
            sslSocketFactory,
            trustAllCerts[0] as X509TrustManager
        )
//        builder.hostnameVerifier(HostnameVerifier { hostname, session ->  })
        builder.hostnameVerifier { hostname, session -> true }
        val cacheSize = 10 * 1024 * 1024 // 10 MB

        val cache = Cache(context.cacheDir, cacheSize.toLong())
        return OkHttpClient.Builder()
            .connectTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(15, TimeUnit.SECONDS) // write timeout
            .readTimeout(30, TimeUnit.SECONDS) // read timeout
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .authenticator(authenticator)
            // .cookieJar(cookieJar)
            .cache(cache)
            .build()
    }


    @Provides
    fun provideLetstrackPreference(preferences: SharedPreferences?): AppPrefs {
        return AppPrefs(preferences)
    }

    @Provides
    fun providePreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(APP_NAME, Context.MODE_PRIVATE)
    }

    @Provides
    @Named(TOKEN)
    fun provideToken(preference: AppPrefs): String {
        return preference.TOKEN
    }

    @Provides
    @Named(REFRESH_TOKEN)
    fun provideRefreshToken(preference: AppPrefs): String {
        return preference.refreshToken
    }

    @Provides
    fun provideDeviceToken(preference: AppPrefs): String {
        return preference.deviceToken
    }


    @Provides
    @NotNull
    fun provideAuth(@ApplicationContext @NotNull context: Context): OnAuthFailed {
        return object : OnAuthFailed {
            override fun onFailedAuth() {
            }
        }
    }

    @Provides
    fun provideDeviceId(@ApplicationContext context: Context, preference: AppPrefs): String {
        preference.deviceId =
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        return preference.deviceId
    }

    @Singleton
    @Provides
    fun provideAuthorizationInterceptor(
        preference: AppPrefs,
        connection: CheckInternetConnection,
    ): Interceptor {
        return Interceptor { chain ->
            var request = chain.request()
            val requestBuilder = request.newBuilder()
            //  .addHeader("Authorization", "Bearer " + token);
            val cacheControl = CacheControl.Builder()
                .maxAge(60, TimeUnit.SECONDS)
                .build()
            if (connection.isNetworkAvailable()) {
                val maxAge = 30 // read from cache for 1 minute
                //request.newBuilder()
                requestBuilder
                    .addHeader("X-Requested-With", "XMLHttpRequest")
                    .addHeader(
                        "Authorization",
                        "Bearer ${preference.TOKEN}"
                    )
                    .addHeader("Content-Type", "application/json")
                    .header("Cache-Control", "public, max-age=$maxAge")
                    .cacheControl(cacheControl)
                    .build()
                request = requestBuilder.build()
                var response = chain.proceed(request)
                var tryCount = 0
                while (!response.isSuccessful && tryCount < 3 && response.code == 504) {
                    Log.d("intercept", "Request is not successful - $tryCount")
                    tryCount++
                    // retry the request
                    response = chain.proceed(request)
                }

                if (!response.isSuccessful && response.code == 504)
                    throw NetworkConnectivityError()
                else
                    response

            } else {
                val maxStale = 60 * 60 * 24 * 28 // tolerate 4-weeks stale
                // request.newBuilder()
                requestBuilder
                    .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
                    .build()
                request = requestBuilder.build()
                chain.proceed(request)
            }

        }
    }

    @Singleton
    @Provides
    fun OAuth2Authenticator(
        preference: AppPrefs,
        onAuthFailed: OnAuthFailed
    ): Authenticator {
        return okhttp3.Authenticator { route: Route?, response: Response? ->
            if (response!!.code == 401) {
//
            }
            if (onAuthFailed != null) {
                //preference.isLogin = false
                onAuthFailed.onFailedAuth()
            }
            return@Authenticator null
        }
    }

    @Provides
    @Singleton
    fun provideRealm(): Realm = Realm.getDefaultInstance()

    @Singleton // Provide always the same instance
    @Provides
    fun providesCoroutineScope(): CoroutineScope {
        // Run this code when providing an instance of CoroutineScope
        return CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }
}