package com.yanli.myshop.livaData

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.*
import com.yanli.myshop.model.Item


class FirestoreQueryLiveData : LiveData<List<Item>>(), EventListener<QuerySnapshot> {
    var query = FirebaseFirestore.getInstance().collection("items")
        .orderBy("viewCount", Query.Direction.DESCENDING)
        .limit(10)
    private lateinit var registration: ListenerRegistration

    var isRegistration = false


    override fun onActive() {
        registration = query.addSnapshotListener(this)
        isRegistration = true
    }

    override fun onInactive() {
        super.onInactive()
        if (isRegistration) {
            registration.remove()
        }
    }

    override fun onEvent(querySnapshot: QuerySnapshot?, exception: FirebaseFirestoreException?) {
        if (querySnapshot != null && !querySnapshot.isEmpty) {
            val list = mutableListOf<Item>()
            for (document in querySnapshot.documents) {
                val item = document.toObject(Item::class.java) ?: Item()
                item.id = document.id
                list.add(item)
            }
            value = list
        }
    }

    fun setCategory(categoryId: String) {
        if (isRegistration) {
            registration.remove()
            isRegistration = false
        }
        if (categoryId.isNotEmpty()) {
            query = FirebaseFirestore.getInstance().collection("items")
                .whereEqualTo("category", categoryId)
                .orderBy("viewCount", Query.Direction.DESCENDING)
                .limit(10)
        } else {
            query = FirebaseFirestore.getInstance().collection("items")
                .orderBy("viewCount", Query.Direction.DESCENDING)
                .limit(10)
        }
        registration = query.addSnapshotListener(this)
        isRegistration = true
    }
}
