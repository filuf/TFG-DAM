package com.raj.slotify.room
import android.app.Application
import androidx.room.Room
import com.raj.slotify.room.database.TokenDatabase

class TokenApp: Application() {

    companion object {
        lateinit var database: TokenDatabase
    }

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(this, TokenDatabase::class.java, "token_database")
            .fallbackToDestructiveMigration(false).build()
    }

}