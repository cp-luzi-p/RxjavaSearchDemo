package com.example.rxjavasearchdemo

import com.example.rxjavasearchdemo.model.Contact
import io.reactivex.Observable
import io.reactivex.Single

import retrofit2.http.GET
import retrofit2.http.Query


interface ApiServices {

    @GET("contacts.php")
    fun getContacts(@Query("source") source: String?, @Query("search") query: String?) :
            Single<List<Contact>>

}