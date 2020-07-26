package com.yanli.myshop.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yanli.myshop.model.Item

@Dao
interface ItemDao {
    @Query("select * from Item order by viewCount")
    fun getItems(): LiveData<List<Item>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addItem(item: Item)

    @Query("select * from item where category == :categoryId order by viewCount")
    fun getItemsByCategoryId(categoryId: String): LiveData<List<Item>>
}