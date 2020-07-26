package com.yanli.myshop

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import androidx.lifecycle.LiveData
import com.yanli.myshop.dao.ItemDao
import com.yanli.myshop.database.ItemDatabase
import com.yanli.myshop.livaData.FirestoreQueryLiveData
import com.yanli.myshop.model.Item

class ItemRepository(application: Application) {
    private var itemDao: ItemDao
    private var network = false

    private lateinit var items: LiveData<List<Item>>
    private var firestoreQueryLiveData = FirestoreQueryLiveData()

    init {
        itemDao = ItemDatabase.getDatabase(application).getItemDao()
        items = itemDao.getItems()
        val cm = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = cm.activeNetworkInfo
        network = networkInfo.isConnected
    }

    fun getAllItems(): LiveData<List<Item>> {
        if (network) {
            items = firestoreQueryLiveData
        } else {
            items = itemDao.getItems()
        }
        return items
    }

    fun setCategory(categoryId: String) {
        if (network) {
            firestoreQueryLiveData.setCategory(categoryId)
        } else {
            items = itemDao.getItemsByCategoryId(categoryId)
        }
    }
}