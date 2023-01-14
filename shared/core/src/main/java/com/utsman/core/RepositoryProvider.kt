package com.utsman.core

import com.utsman.core.extensions.reducer
import com.utsman.core.state.StateEvent
import kotlinx.coroutines.flow.MutableStateFlow
import retrofit2.Response

open class RepositoryProvider {

    suspend fun <T, U>bindToState(stateFlow: MutableStateFlow<StateEvent<U>>, onFetch: suspend () -> Response<T>, mapper: (T) -> U) {
        stateFlow.value = StateEvent.Idle()
        val result = onFetch.invoke().reducer(mapper)
        stateFlow.value = result
    }
}