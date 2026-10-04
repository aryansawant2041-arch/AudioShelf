package com.example.audioshelf

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val usernameInput = findViewById<EditText>(R.id.usernameInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val loginButton = findViewById<Button>(R.id.loginButton)

        val db = AppDatabase.getDatabase(this)

        loginButton.setOnClickListener {
            val username = usernameInput.text.toString().trim()
            val password = passwordInput.text.toString()

            if (username.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val user = db.userDao().getUser(username)
                if (user != null && user.password == password) {
                    getSharedPreferences("session", MODE_PRIVATE).edit().putString("logged_in_user", username).apply()
                    startActivity(Intent(this@MainActivity, MainHostActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@MainActivity, "Invalid username or password", Toast.LENGTH_SHORT).show()
                }
            }
        }
        findViewById<TextView>(R.id.goToRegisterText).setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}