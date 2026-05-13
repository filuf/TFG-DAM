package com.raj.slotify.room.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface TokenDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertToken(token: TokenEntity)

    @Query("SELECT * FROM tokens")
    suspend fun getAllTokens(): List<TokenEntity>

    @Query("SELECT * FROM tokens WHERE id = :id")
    suspend fun getTokenById(id: Long): TokenEntity

    @Query("SELECT * FROM tokens ORDER BY id DESC LIMIT 1")
    suspend fun getLastToken(): TokenEntity

    @Query("DELETE FROM tokens WHERE id = :id")
    suspend fun deleteTokenById(id: Long)

    @Query("DELETE FROM tokens")
    suspend fun deleteAllTokens()

}