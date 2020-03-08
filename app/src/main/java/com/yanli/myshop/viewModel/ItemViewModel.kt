package com.yanli.myshop.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.yanli.myshop.livaData.FirestoreQueryLiveData
import com.yanli.myshop.model.Item

class ItemViewModel : ViewModel() {
    private var items = MutableLiveData<List<Item>>()

    private var firestoreQueryLiveData = FirestoreQueryLiveData()

    fun getItems(): FirestoreQueryLiveData {

//        FirebaseFirestore.getInstance().collection("items")
//            .orderBy("viewCount", Query.Direction.DESCENDING)
//            .limit(10)
//            .addSnapshotListener { querySnapshot, exception ->
//                if (querySnapshot != null && !querySnapshot.isEmpty) {
//                    var list = mutableListOf<Item>()
//                    for(document in querySnapshot.documents){
//                        val item = document.toObject(Item::class.java)?:Item()
//                        item.id = document.id
//                        list.add(item)
//                    }
//                    items.value = list
//                }
//            }
//        return items

        return firestoreQueryLiveData
    }
}