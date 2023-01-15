package com.utsman.core.extensions

import android.util.MalformedJsonException
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonSyntaxException
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

fun <T> LiveData<StateEvent<T>>.subscribe(
    lifecycleOwner: LifecycleOwner,
    subscriber: StateEventSubscriber<T>
) {
    observe(lifecycleOwner) {
        when (it) {
            is StateEvent.Idle -> subscriber.onIdle()
            is StateEvent.Loading -> subscriber.onLoading()
            is StateEvent.Success -> subscriber.onSuccess(it.data)
            is StateEvent.Failure -> subscriber.onFailure(it.exception)
            is StateEvent.Empty -> subscriber.onEmpty()
        }
    }
}

fun <T> Flow<T>.mapStateEvent(): FlowState<T> {
    return this.catch {
        StateEvent.Failure<T>(it)
    }.map {
        StateEvent.Success(it)
    }
}

fun <T, U> Response<T>.reducer(mapper: (T) -> U): StateEvent<U> {
    return try {
        val body = body()
        if (isSuccessful && body != null) {
            try {
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
            } catch (e: JsonSyntaxException) {
                println("OJEKUUU======== network json failure")
                StateEvent.Failure(e)
            } catch (e: Throwable) {
                println("OJEKUUU======== network failure")
                StateEvent.Failure(e)
            }

        } else {
            val throwable = StateApiException(message(), code())
            StateEvent.Failure(throwable)
        }
    } catch (e: Throwable) {
        StateEvent.Failure(e)
    } catch (e: MalformedJsonException) {
        StateEvent.Failure(e)
    } catch (e: JsonSyntaxException) {
        StateEvent.Failure(e)
    }
}

fun <T, U> Response<T>.asFlowStateEvent(mapper: (T) -> U): FlowState<U> {
    return flow {
        val emitData = try {
            val body = body()
            if (isSuccessful && body != null) {
                try {
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
                } catch (e: JsonSyntaxException) {
                    StateEvent.Failure(e)
                } catch (e: Throwable) {
                    StateEvent.Failure(e)
                }

            } else {
                val throwable = StateApiException(message(), code())
                StateEvent.Failure(throwable)
            }
        } catch (e: Throwable) {
            StateEvent.Failure(e)
        } catch (e: MalformedJsonException) {
            StateEvent.Failure(e)
        } catch (e: JsonSyntaxException) {
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

fun <T> StateEvent<T>.onSuccess(action: T.() -> Unit) {
    if (this is StateEvent.Success) {
        action.invoke(data)
    }
}

fun <T> StateEvent<T>.onLoading(action: () -> Unit) {
    if (this is StateEvent.Loading) {
        action.invoke()
    }
}

fun <T> StateEvent<T>.onFailure(action: Throwable.() -> Unit) {
    if (this is StateEvent.Failure) {
        action.invoke(exception)
    }
}