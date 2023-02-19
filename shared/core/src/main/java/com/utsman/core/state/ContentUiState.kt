package com.utsman.core.state

sealed class ContentUiState {
    object Content : ContentUiState()
    object Splash : ContentUiState()
}