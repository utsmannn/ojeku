package com.utsman.core.state.exception

class StateApiException(message: String, private val code: Int) : Throwable(message) {

    fun code() = code
}