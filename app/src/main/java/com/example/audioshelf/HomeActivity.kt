package com.example.audioshelf

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        recyclerView = findViewById(R.id.shelfRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        findViewById<android.widget.Button>(R.id.searchButton).setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadShelf()
    }

    private fun loadShelf() {
        val shelf = ShelfManager.getShelf(this)
        val emptyText = findViewById<android.widget.TextView>(R.id.emptyStateText)

        emptyText.visibility = if (shelf.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
        recyclerView.visibility = if (shelf.isEmpty()) android.view.View.GONE else android.view.View.VISIBLE

        recyclerView.adapter = BookAdapter(shelf) { selectedBook ->
            val intent = Intent(this, DetailsActivity::class.java)
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
}