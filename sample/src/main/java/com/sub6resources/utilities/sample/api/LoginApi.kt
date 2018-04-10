package com.sub6resources.utilities.sample.api

import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApi {
    @POST("api/login")
    fun login(@Body login: Login): Single<Token>
}

data class Login(val username: String, val password: String)
data class Token(val token: String)