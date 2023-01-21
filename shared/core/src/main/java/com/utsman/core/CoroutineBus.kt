package com.utsman.core

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class CoroutineBus {

    private data class DataBus<T>(
        val key: String,
        val data: T
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

    companion object : KoinComponent {
        fun getInstance(): CoroutineBus = get()
    }
}