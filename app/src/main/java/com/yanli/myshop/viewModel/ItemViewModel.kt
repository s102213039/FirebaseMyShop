package com.yanli.myshop.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yanli.myshop.livaData.FirestoreQueryLiveData
import com.yanli.myshop.model.Item

class ItemViewModel : ViewModel() {
    private var items = MutableLiveData<List<Item>>()

    private var firestoreQueryLiveData = FirestoreQueryLiveData()

    fun getItems(): FirestoreQueryLiveData {
        return firestoreQueryLiveData
    }

    fun setCategory(categoryId:String){
        firestoreQueryLiveData.setCategory(categoryId)
    }
}