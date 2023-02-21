package com.utsman.ojeku.socket

import io.socket.client.IO
import io.socket.client.Socket
import io.socket.client.SocketIOException
import io.socket.emitter.Emitter
import kotlinx.coroutines.delay
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.net.URISyntaxException

class SocketWrapper {

    private var socket: Socket? = null

    fun connect() {
        socket = try {
            IO.socket("https://a288-2001-448a-2020-7c8b-ac58-3645-5498-daf9.ap.ngrok.io")
        } catch (e: URISyntaxException) {
            e.printStackTrace()
            null
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            null
        } catch (e: SocketIOException) {
            e.printStackTrace()
            println("asuuuuu failure -> ${e.message}")
            null
        }

        socket?.connect()
    }

    suspend fun listen(event: String, listener: String.() -> Unit) {
        socket?.on(event) {
            val data = it[0] as String

            println("asuuuuuuuu event listen -> $data")
            listener.invoke(data)
        }

        delay(2000)
        println("asuuuuu socket is connect: ${socket?.connected()}")
    }

    fun destroy() {
        socket?.disconnect()
    }

    companion object : KoinComponent {
        val instance: SocketWrapper by inject()
    }
}