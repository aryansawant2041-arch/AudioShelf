package com.example.audioshelf

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import java.util.concurrent.TimeUnit

class PlayerActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null
    private var currentBook: Book? = null
    private var isPrepared = false
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val title = intent.getStringExtra("title") ?: "Unknown Title"
        val author = intent.getStringExtra("author") ?: "Unknown Author"
        val coverUrl = intent.getStringExtra("coverUrl") ?: ""
        val audioUrl = intent.getStringExtra("audioUrl") ?: ""

        currentBook = Book(title = title, author = author, duration = "", chapters = 0, listenLink = "", coverUrl = coverUrl, audioUrl = audioUrl)

        findViewById<TextView>(R.id.playerTitle).text = title
        findViewById<TextView>(R.id.playerAuthor).text = author

        Glide.with(this)
            .load(coverUrl)
            .placeholder(android.R.color.darker_gray)
            .error(android.R.color.darker_gray)
            .centerCrop()
            .into(findViewById<ImageView>(R.id.playerCover))

        val playPauseButton = findViewById<Button>(R.id.playerPlayPauseButton)
        val seekBar = findViewById<SeekBar>(R.id.playerSeekBar)
        val timeText = findViewById<TextView>(R.id.playerTimeText)

        if (audioUrl.isBlank()) {
            playPauseButton.text = "No audio available"
            playPauseButton.isEnabled = false
            return
        }

        playPauseButton.setOnClickListener {
            if (mediaPlayer == null) {
                startPlayback(audioUrl, playPauseButton, seekBar, timeText)
            } else if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
                playPauseButton.text = "▶ Play"
            } else {
                mediaPlayer?.start()
                playPauseButton.text = "⏸ Pause"
                updateSeekBar(seekBar, timeText)
            }
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser && isPrepared) mediaPlayer?.seekTo(progress)
            }
            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })

        val dndSwitch = findViewById<com.google.android.material.switchmaterial.SwitchMaterial>(R.id.dndSwitch)
        dndSwitch.setOnCheckedChangeListener { _, isChecked ->
            setDndEnabled(isChecked)
        }
    }

    private fun startPlayback(url: String, button: Button, seekBar: SeekBar, timeText: TextView) {
        button.isEnabled = false
        button.text = "Loading..."
        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            setOnPreparedListener {
                isPrepared = true
                seekBar.max = duration
                button.isEnabled = true
                button.text = "⏸ Pause"
                start()
                updateSeekBar(seekBar, timeText)
            }
            setOnErrorListener { _, _, _ ->
                Toast.makeText(this@PlayerActivity, "Playback error — try again", Toast.LENGTH_SHORT).show()
                button.isEnabled = true
                button.text = "▶ Play"
                true
            }
            prepareAsync()
        }
    }

    private fun updateSeekBar(seekBar: SeekBar, timeText: TextView) {
        val player = mediaPlayer ?: return
        if (player.isPlaying) {
            seekBar.progress = player.currentPosition
            timeText.text = "${formatTime(player.currentPosition)} / ${formatTime(player.duration)}"
            currentBook?.let { PlaybackManager.saveProgress(this, it, player.currentPosition) }
            handler.postDelayed({ updateSeekBar(seekBar, timeText) }, 500)
        }
    }

    private fun formatTime(ms: Int): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(ms.toLong())
        val seconds = TimeUnit.MILLISECONDS.toSeconds(ms.toLong()) % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
        if (hasDndPermission()) {
            setDndEnabled(false)
        }
    }

    private fun hasDndPermission(): Boolean {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as android.app.NotificationManager
        return notificationManager.isNotificationPolicyAccessGranted
    }

    private fun setDndEnabled(enabled: Boolean) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as android.app.NotificationManager
        if (!hasDndPermission()) {
            Toast.makeText(this, "Please allow Notification Access for AudioShelf to enable DND", Toast.LENGTH_LONG).show()
            startActivity(android.content.Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS))
            return
        }
        notificationManager.setInterruptionFilter(
            if (enabled) android.app.NotificationManager.INTERRUPTION_FILTER_PRIORITY
            else android.app.NotificationManager.INTERRUPTION_FILTER_ALL
        )
    }
}