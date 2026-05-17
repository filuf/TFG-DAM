package com.raj.slotify.room.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [TokenEntity::class], version = 2)
abstract class TokenDatabase: RoomDatabase() {

    abstract fun tokenDao(): TokenDao

}