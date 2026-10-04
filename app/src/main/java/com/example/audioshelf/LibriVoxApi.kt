package com.example.audioshelf

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Call

interface LibriVoxApi {
    @GET("api/feed/audiobooks/")
    fun searchBooks(
        @Query("title") title: String,
        @Query("format") format: String = "json",
        @Query("extended") extended: Int = 1,
        @Query("coverart") coverart: Int = 1
    ): Call<LibriVoxResponse>

    @GET("api/feed/audiobooks/")
    fun browseBooks(
        @Query("format") format: String = "json",
        @Query("extended") extended: Int = 1,
        @Query("coverart") coverart: Int = 1,
        @Query("limit") limit: Int = 10
    ): Call<LibriVoxResponse>
}