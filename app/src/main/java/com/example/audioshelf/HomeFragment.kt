package com.example.audioshelf

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment(R.layout.activity_home) {

    private lateinit var shelfRecyclerView: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        shelfRecyclerView = view.findViewById(R.id.shelfRecyclerView)
        shelfRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        view.findViewById<android.widget.Button>(R.id.searchButton).setOnClickListener {
            (requireActivity() as MainHostActivity).switchToSearchTab()
        }

        val suggestionsRV = view.findViewById<RecyclerView>(R.id.suggestionsRecyclerView)
        val topListenedRV = view.findViewById<RecyclerView>(R.id.topListenedRecyclerView)
        suggestionsRV.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        topListenedRV.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        loadBrowseSections(suggestionsRV, topListenedRV)

        view.findViewById<android.widget.ImageButton>(R.id.menuButton).setOnClickListener { anchor ->
            val popup = androidx.appcompat.widget.PopupMenu(requireContext(), anchor)
            popup.menuInflater.inflate(R.menu.home_options_menu, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_about -> {
                        android.widget.Toast.makeText(requireContext(), "AudioShelf — free audiobooks from LibriVox", android.widget.Toast.LENGTH_LONG).show()
                        true
                    }
                    R.id.menu_logout -> {
                        requireActivity().getSharedPreferences("session", android.content.Context.MODE_PRIVATE).edit().clear().apply()
                        startActivity(Intent(requireContext(), MainActivity::class.java))
                        requireActivity().finish()
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }

    override fun onResume() {
        super.onResume()
        loadShelf()
        loadCurrentlyListening()
    }

    private fun openDetails(book: Book) {
        val intent = Intent(requireContext(), DetailsActivity::class.java)
        intent.putExtra("title", book.title)
        intent.putExtra("author", book.author)
        intent.putExtra("duration", book.duration)
        intent.putExtra("chapters", book.chapters)
        intent.putExtra("listenLink", book.listenLink)
        intent.putExtra("coverUrl", book.coverUrl)
        intent.putExtra("genre", book.genre)
        intent.putExtra("language", book.language)
        intent.putExtra("audioUrl", book.audioUrl)
        startActivity(intent)
    }

    private fun loadShelf() {
        val shelf = ShelfManager.getShelf(requireContext())
        val emptyText = requireView().findViewById<android.widget.TextView>(R.id.emptyStateText)

        emptyText.visibility = if (shelf.isEmpty()) View.VISIBLE else View.GONE
        shelfRecyclerView.visibility = if (shelf.isEmpty()) View.GONE else View.VISIBLE

        shelfRecyclerView.adapter = BookAdapter(shelf) { openDetails(it) }
    }

    private fun loadCurrentlyListening() {
        val section = requireView().findViewById<View>(R.id.currentlyListeningSection)
        val recyclerView = requireView().findViewById<RecyclerView>(R.id.currentlyListeningRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        val currentBook = PlaybackManager.getCurrentBook(requireContext())
        if (currentBook != null) {
            section.visibility = View.VISIBLE
            recyclerView.adapter = BookCardAdapter(listOf(currentBook)) { book ->
                val intent = Intent(requireContext(), PlayerActivity::class.java)
                intent.putExtra("title", book.title)
                intent.putExtra("author", book.author)
                intent.putExtra("coverUrl", book.coverUrl)
                intent.putExtra("audioUrl", book.audioUrl)
                startActivity(intent)
            }
        } else {
            section.visibility = View.GONE
        }
    }

    private fun loadBrowseSections(suggestionsRV: RecyclerView, topListenedRV: RecyclerView) {
        RetrofitClient.api.browseBooks(limit = 10).enqueue(object : Callback<LibriVoxResponse> {
            override fun onResponse(call: Call<LibriVoxResponse>, response: Response<LibriVoxResponse>) {
                if (!isAdded) return
                val librivoxBooks = response.body()?.books ?: emptyList()

                val books = librivoxBooks.map { lvBook ->
                    val authorName = lvBook.authors?.firstOrNull()?.let {
                        "${it.first_name ?: ""} ${it.last_name ?: ""}".trim()
                    } ?: "Unknown Author"
                    Book(
                        title = lvBook.title,
                        author = authorName,
                        duration = lvBook.totaltime ?: "Unknown",
                        chapters = lvBook.num_sections?.toIntOrNull() ?: 0,
                        listenLink = lvBook.url_librivox ?: "",
                        coverUrl = lvBook.coverart_thumbnail ?: "",
                        genre = lvBook.genres?.firstOrNull()?.name ?: "Unknown",
                        language = lvBook.language ?: "English",
                        audioUrl = lvBook.sections?.firstOrNull()?.listen_url ?: ""
                    )
                }

                // Split one batch into two sections — simple, honest placeholder
                // since we don't track real listening analytics
                val half = books.size / 2
                suggestionsRV.adapter = BookCardAdapter(books.take(half)) { openDetails(it) }
                topListenedRV.adapter = BookCardAdapter(books.drop(half)) { openDetails(it) }
            }

            override fun onFailure(call: Call<LibriVoxResponse>, t: Throwable) {
                // Silently ignore — Home still works fine without these sections loading
            }
        })
    }
}