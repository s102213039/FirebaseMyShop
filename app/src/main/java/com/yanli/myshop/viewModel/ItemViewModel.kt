package com.yanli.myshop.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.yanli.myshop.ItemRepository
import com.yanli.myshop.model.Item

class ItemViewModel(application: Application) : AndroidViewModel(application) {
    private lateinit var itemRepository: ItemRepository

    init {
        itemRepository = ItemRepository(application)
    }

    fun getItems(): LiveData<List<Item>> {
        return itemRepository.getAllItems()
    }

    fun setCategory(categoryId: String) {
        itemRepository.setCategory(categoryId)
    }
}