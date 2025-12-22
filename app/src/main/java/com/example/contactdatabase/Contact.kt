package com.example.contactdatabase

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Contact(
    val id: Long = 0,
    val name: String,
    val phone: String,
    val email: String = "",
    val avatarResourceId: Int = R.drawable.avatar,
    val avatarUri: String? = null
) : Parcelable
