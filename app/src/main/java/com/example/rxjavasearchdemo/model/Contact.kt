package com.example.rxjavasearchdemo.model

import com.google.gson.annotations.SerializedName

class Contact {

    @SerializedName("name")
    private var name: String = " "

    @SerializedName("image")
    private var profileImage: String = " "

    @SerializedName("phone")
    private var phone: String = " "

    @SerializedName("email")
    private var email: String = " "

    fun getName(): String {
        return name
    }

    fun getEmail(): String {
        return email
    }

    fun getPhone(): String {
        return phone
    }

    fun getProfileImage(): String {
        return profileImage
    }


    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + profileImage.hashCode()
        result = 31 * result + phone.hashCode()
        result = 31 * result + email.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (other != null && (other is Contact )) {
            return other.getEmail().equals(email, ignoreCase = true)
        }
        return false
    }
}