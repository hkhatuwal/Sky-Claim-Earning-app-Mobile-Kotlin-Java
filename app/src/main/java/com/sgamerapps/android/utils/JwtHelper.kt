package com.sgamerapps.android.utils

import android.os.Build
import androidx.annotation.RequiresApi
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm


class JwtHelper(private val secret: String) {

    @RequiresApi(Build.VERSION_CODES.N)
    fun generateToken(data: Map<String, String>): String {
        var builder = Jwts.builder()
        data.forEach { (key, value) ->
            builder.claim(key, value)
        }

        builder.signWith(SignatureAlgorithm.HS256, secret.toByteArray())
        return builder.compact()
    }
}