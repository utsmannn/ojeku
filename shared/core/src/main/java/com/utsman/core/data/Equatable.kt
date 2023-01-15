package com.utsman.core.data

interface Equatable {
    val uniqueId: String
    val longId: Long
    fun isEqual(equatable: Equatable): Boolean
}