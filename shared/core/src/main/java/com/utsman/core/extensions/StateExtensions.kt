package com.utsman.core.extensions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.reflect.TypeToken
import com.utsman.core.state.FlowState
import com.utsman.core.state.exception.StateApiException
import com.utsman.core.state.StateEvent
import com.utsman.core.state.StateEventManager
import com.utsman.core.state.StateEventSubscriber
import com.utsman.core.state.exception.StateDataEmptyException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import retrofit2.Response

val <T>StateEvent<T>.value: T?
    get() {
        return if (this is StateEvent.Success) {
            this.data
        } else {
            null
        }
    }


fun <T> ViewModel.convertEventToSubscriber(
    eventManager: StateEventManager<T>,
    subscriber: StateEventSubscriber<T>
) {
    eventManager.subscribe(
        scope = viewModelScope,
        onIdle = subscriber::onIdle,
        onLoading = subscriber::onLoading,
        onFailure = subscriber::onFailure,
        onSuccess = subscriber::onSuccess,
        onEmpty = subscriber::onEmpty
    )
}

fun <T>Flow<T>.mapStateEvent(): FlowState<T> {
    return this.catch {
        StateEvent.Failure<T>(it)
    }.map {
        StateEvent.Success(it)
    }
}

fun <T, U>Response<T>.asFlowStateEvent(mapper: (T) -> U): FlowState<U> {
    return flow {
        emit(StateEvent.Loading())
        delay(2000)
        val emitData = try {
            val body = body()
            if (isSuccessful && body != null) {
                val data = mapper.invoke(body)
                if (data is List<*>) {
                    if (data.isEmpty()) {
                        StateEvent.Empty()
                    } else {
                        StateEvent.Success(data as U)
                    }
                } else {
                    StateEvent.Success(data)
                }

            } else {
                val throwable = StateApiException(message(), code())
                StateEvent.Failure(throwable)
            }
        } catch (e: Throwable) {
            StateEvent.Failure(e)
        }

        emit(emitData)
    }
}

fun Throwable.ifStateEmpty(action: (StateDataEmptyException) -> Unit) {
    if (this is StateDataEmptyException) {
        action.invoke(this)
    }
}

fun Throwable.ifNetworkError(action: (StateApiException) -> Unit) {
    if (this is StateApiException) {
        action.invoke(this)
    }
}