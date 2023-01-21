package com.utsman.core.extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

@Suppress("FunctionName")
fun IOScope(): CoroutineScope = object : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + SupervisorJob()
}