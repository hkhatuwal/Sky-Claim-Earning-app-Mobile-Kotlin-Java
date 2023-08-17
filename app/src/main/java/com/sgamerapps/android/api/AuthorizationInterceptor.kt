package com.sgamerapps.android.api

import com.sgamerapps.android.config.AppConfig
import okhttp3.Interceptor
import okhttp3.Response

class AuthorizationInterceptor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        var token=AppConfig.getToken();
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()

        return chain.proceed(request)
    }
}