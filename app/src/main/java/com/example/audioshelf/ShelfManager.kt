package com.example.audioshelf

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object ShelfManager {
    private const val PREFS_NAME = "audioshelf_prefs"
    private const val SHELF_KEY = "shelf_books"

    fun getShelf(context: Context): MutableList<Book> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(SHELF_KEY, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<Book>>() {}.type
        return Gson().fromJson(json, type)
    }

    fun addToShelf(context: Context, book: Book) {
        val shelf = getShelf(context)
        val alreadyExists = shelf.any { it.title == book.title && it.author == book.author }
        if (!alreadyExists) {
            shelf.add(book)
            saveShelf(context, shelf)
        }
    }

    fun removeFromShelf(context: Context, book: Book) {
        val shelf = getShelf(context)
        shelf.removeAll { it.title == book.title && it.author == book.author }
        saveShelf(context, shelf)
    }

    private fun saveShelf(context: Context, shelf: MutableList<Book>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = Gson().toJson(shelf)
        prefs.edit().putString(SHELF_KEY, json).apply()
    }
}