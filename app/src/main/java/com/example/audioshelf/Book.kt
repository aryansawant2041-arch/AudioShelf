package com.example.audioshelf

data class Book(
    val title: String,
    val author: String,
    val duration: String,
    val chapters: Int,
    val listenLink: String,
    val coverUrl: String = "",
    val genre: String = "",
    val language: String = "",
    val audioUrl: String = ""
)