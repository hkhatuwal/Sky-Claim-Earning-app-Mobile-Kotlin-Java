package com.sgamerapps.android.api

import com.sgamerapps.android.config.MyApp
import com.sgamerapps.android.utils.Helper
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException


class ApiClient {

    companion object {

        private var apiInstance: Api? = null
        private var baseUrl: String? = null

        fun getInstance(): Api {
            assert(baseUrl != null) { "Base url not set  call ApiConfig.setBaseUrl() before calling api" }

            if (apiInstance == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(getClient())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                apiInstance = retrofit.create(Api::class.java)
            }
            return apiInstance!!
        }

        fun setBaseUrl(url: String) {
            baseUrl = url
        }

        private fun getClient(): OkHttpClient {

            return OkHttpClient.Builder()
                .addInterceptor(AuthorizationInterceptor())
                .addInterceptor(object : Interceptor {
                    override fun intercept(chain: Interceptor.Chain): Response {
                        if (!Helper.isConnectingToInternet(MyApp.context)) {

                            throw IOException("No internet")
                        }
                        return chain.proceed(chain.request())
                    }

                })
                .build()
        }

    }
}