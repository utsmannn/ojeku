package com.utsman.core.data

open class EquatableProvider(private val identifier: String): Equatable {
    override val uniqueId: String = identifier

    override val longId: Long
        get() = identifier.longId()

    override fun isEqual(equatable: Equatable): Boolean {
        return uniqueId == equatable.uniqueId
    }
}

fun String.longId(): Long {
    val regexString = REGEX_STRING.toRegex()
    val regexNumber = REGEX_NUMBER.toRegex()
    val outputChar = regexString.replace(this, "")
    val outputNumber = regexNumber.replace(this, "")

    val charArray = outputChar.lowercase().toCharArray()
    val resultString = charArray.map {
        val temp = it.code
        val code = CHAR_CODE
        if (temp in CHAR_MIN..CHAR_MAX) {
            temp - code
        } else {
            null
        }
    }.filterNotNull().sum().toString()

    val finalNum = outputNumber.toLongOrNull() ?: 0L
    val finalString = resultString.toLongOrNull() ?: 0L
    return finalNum + finalString
}

private const val CHAR_CODE = 96
private const val CHAR_MIN = 97
private const val CHAR_MAX = 122

private const val REGEX_STRING = "[^A-Za-z]+"
private const val REGEX_NUMBER = """[^0-9]"""