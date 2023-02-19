package com.utsman.core

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class CoroutineBus {

    private data class DataBus<T>(
        val key: String,
        val data: T,
        val time: Long = System.currentTimeMillis()
    )

    private val stateBus: MutableStateFlow<DataBus<*>?> = MutableStateFlow(null)

    fun <T> post(key: String, data: T) {
        val dataBus = DataBus(key, data)
        stateBus.value = dataBus
    }

    fun <T> getLiveData(key: String, coroutineScope: CoroutineScope): LiveData<T> {
        return stateBus
            .filterNotNull()
            .filter { it.key == key }
            .map { it.data as T }
            .asLiveData(coroutineScope.coroutineContext)
    }

    suspend fun <T> getFlow(key: String, coroutineScope: CoroutineScope): StateFlow<T> {
        return stateBus
            .filterNotNull()
            .filter { it.key == key }
            .map { it.data as T }
            .stateIn(coroutineScope)
    }

    companion object : KoinComponent {
        fun getInstance(): CoroutineBus = get()
    }
}