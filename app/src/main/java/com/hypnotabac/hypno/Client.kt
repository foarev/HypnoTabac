package com.hypnotabac.hypno

data class Client(
    val userID: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val hypnoID: String,
    val isRegistered: Boolean
)