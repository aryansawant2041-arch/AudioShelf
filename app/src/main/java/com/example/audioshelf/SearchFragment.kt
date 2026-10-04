package com.example.audioshelf

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment(R.layout.activity_search) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchInput = view.findViewById<EditText>(R.id.searchInput)
        recyclerView = view.findViewById(R.id.searchRecyclerView)
        progressBar = view.findViewById(R.id.searchProgress)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        searchInput.setOnEditorActionListener { _, actionId, event ->
            val isSearchAction = actionId == EditorInfo.IME_ACTION_SEARCH
            val isEnterKey = event != null && event.keyCode == KeyEvent.KEYCODE_ENTER
            if (isSearchAction || isEnterKey) {
                val query = searchInput.text.toString().trim()
                if (query.isNotBlank()) performSearch(query)
                true
            } else {
                false
            }
        }
    }

    private fun performSearch(query: String) {
        progressBar.visibility = View.VISIBLE

        RetrofitClient.api.searchBooks(title = "^$query").enqueue(object : Callback<LibriVoxResponse> {
            override fun onResponse(call: Call<LibriVoxResponse>, response: Response<LibriVoxResponse>) {
                if (!isAdded) return
                progressBar.visibility = View.GONE

                val librivoxBooks = response.body()?.books ?: emptyList()
                if (librivoxBooks.isEmpty()) {
                    Toast.makeText(requireContext(), "No results found", Toast.LENGTH_SHORT).show()
                }

                val books = librivoxBooks.map { lvBook ->
                    val authorName = lvBook.authors?.firstOrNull()?.let {
                        "${it.first_name ?: ""} ${it.last_name ?: ""}".trim()
                    } ?: "Unknown Author"

                    val genreName = lvBook.genres?.firstOrNull()?.name ?: "Unknown"
                    val firstChapterUrl = lvBook.sections?.firstOrNull()?.listen_url ?: ""

                    Book(
                        title = lvBook.title,
                        author = authorName,
                        duration = lvBook.totaltime ?: "Unknown",
                        chapters = lvBook.num_sections?.toIntOrNull() ?: 0,
                        listenLink = lvBook.url_librivox ?: "",
                        coverUrl = lvBook.coverart_thumbnail ?: "",
                        genre = genreName,
                        language = lvBook.language ?: "English",
                        audioUrl = firstChapterUrl
                    )
                }

                recyclerView.adapter = BookAdapter(books) { selectedBook ->
                    val intent = android.content.Intent(requireContext(), DetailsActivity::class.java)
                    intent.putExtra("title", selectedBook.title)
                    intent.putExtra("author", selectedBook.author)
                    intent.putExtra("duration", selectedBook.duration)
                    intent.putExtra("chapters", selectedBook.chapters)
                    intent.putExtra("listenLink", selectedBook.listenLink)
                    intent.putExtra("coverUrl", selectedBook.coverUrl)
                    intent.putExtra("genre", selectedBook.genre)
                    intent.putExtra("language", selectedBook.language)
                    intent.putExtra("audioUrl", selectedBook.audioUrl)
                    startActivity(intent)
                }
            }

            override fun onFailure(call: Call<LibriVoxResponse>, t: Throwable) {
                if (!isAdded) return
                progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), "LibriVox is slow to respond right now — try again in a moment", Toast.LENGTH_LONG).show()
            }
        })
    }
}