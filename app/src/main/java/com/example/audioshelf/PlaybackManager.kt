package com.example.audioshelf

import android.content.Context
import com.google.gson.Gson

object PlaybackManager {
    private const val PREFS_NAME = "playback_prefs"
    private const val CURRENT_KEY = "current_book"
    private const val POSITION_KEY = "current_position"

    fun saveProgress(context: Context, book: Book, positionMs: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(CURRENT_KEY, Gson().toJson(book))
            .putInt(POSITION_KEY, positionMs)
            .apply()
    }

    fun getCurrentBook(context: Context): Book? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(CURRENT_KEY, null) ?: return null
        return Gson().fromJson(json, Book::class.java)
    }

    fun getPosition(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(POSITION_KEY, 0)
    }
}