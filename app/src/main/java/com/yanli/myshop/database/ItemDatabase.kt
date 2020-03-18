package com.yanli.myshop.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.yanli.myshop.dao.ItemDao
import com.yanli.myshop.model.Item

@Database(entities = [Item::class], version = 1)
abstract class ItemDatabase : RoomDatabase() {
    abstract fun getItemDao(): ItemDao

    companion object {
        private lateinit var context: Context
        private val database: ItemDatabase by lazy {
            Room.databaseBuilder(context, ItemDatabase::class.java, "mydb")
                .allowMainThreadQueries()
                .build()
        }

        fun getDatabase(context: Context): ItemDatabase? {
            this.context = context
            return database
        }
    }
}