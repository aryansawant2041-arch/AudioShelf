package com.example.audioshelf

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.concurrent.TimeUnit

class DetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val title = intent.getStringExtra("title") ?: "Unknown Title"
        val author = intent.getStringExtra("author") ?: "Unknown Author"
        val duration = intent.getStringExtra("duration") ?: ""
        val chapters = intent.getIntExtra("chapters", 0)
        val listenLink = intent.getStringExtra("listenLink") ?: ""
        val genre = intent.getStringExtra("genre") ?: "Unknown"
        val language = intent.getStringExtra("language") ?: "English"
        val audioUrl = intent.getStringExtra("audioUrl") ?: ""
        val coverUrl = intent.getStringExtra("coverUrl") ?: ""

        findViewById<Button>(R.id.listenButton).setOnClickListener {
            if (audioUrl.isBlank()) {
                Toast.makeText(this, "No audio available for this book", Toast.LENGTH_SHORT).show()
            } else {
                val playerIntent = android.content.Intent(this, PlayerActivity::class.java)
                playerIntent.putExtra("title", title)
                playerIntent.putExtra("author", author)
                playerIntent.putExtra("coverUrl", coverUrl)
                playerIntent.putExtra("audioUrl", audioUrl)
                startActivity(playerIntent)
            }
        }

        val book = Book(title, author, duration, chapters, listenLink, coverUrl = coverUrl, genre = genre, language = language, audioUrl = audioUrl)

        findViewById<TextView>(R.id.detailTitle).text = title
        findViewById<TextView>(R.id.detailAuthor).text = "$author · $chapters chapters · $duration"
        findViewById<TextView>(R.id.detailGenre).text = "Genre: $genre"
        findViewById<TextView>(R.id.detailLanguage).text = "Language: $language"

        com.bumptech.glide.Glide.with(this)
            .load(coverUrl)
            .placeholder(android.R.color.darker_gray)
            .error(android.R.color.darker_gray)
            .centerCrop()
            .into(findViewById(R.id.detailCover))

        val saveButton = findViewById<Button>(R.id.saveButton)
        val removeButton = findViewById<Button>(R.id.removeButton)

        fun refreshButtons() {
            val isSaved = ShelfManager.getShelf(this).any { it.title == book.title && it.author == book.author }
            saveButton.visibility = if (isSaved) android.view.View.GONE else android.view.View.VISIBLE
            removeButton.visibility = if (isSaved) android.view.View.VISIBLE else android.view.View.GONE
        }
        refreshButtons()

        saveButton.setOnClickListener {
            ShelfManager.addToShelf(this, book)
            Toast.makeText(this, "Added to shelf", Toast.LENGTH_SHORT).show()
            refreshButtons()
        }

        removeButton.setOnClickListener {
            ShelfManager.removeFromShelf(this, book)
            Toast.makeText(this, "Removed from shelf", Toast.LENGTH_SHORT).show()
            refreshButtons()
        }
    }
}