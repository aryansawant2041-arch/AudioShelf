package com.example.audioshelf

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {

    @Insert
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUser(username: String): User?

    @Query("SELECT COUNT(*) FROM users WHERE username = :username")
    suspend fun userExists(username: String): Int
}