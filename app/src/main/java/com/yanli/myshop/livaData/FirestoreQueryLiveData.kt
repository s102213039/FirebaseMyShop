package com.yanli.myshop.livaData

import androidx.lifecycle.LiveData
import com.google.firebase.firestore.*


class FirestoreQueryLiveData : LiveData<QuerySnapshot>(), EventListener<QuerySnapshot> {
    var query = FirebaseFirestore.getInstance().collection("items")
        .orderBy("viewCount", Query.Direction.DESCENDING)
        .limit(10)
    private lateinit var listenerRegistration: ListenerRegistration

    var isRegistration = false


    override fun onActive() {
        listenerRegistration = query.addSnapshotListener(this)
        isRegistration = true
    }

    override fun onInactive() {
        super.onInactive()
        if (isRegistration) {
            listenerRegistration.remove()
        }
    }

    override fun onEvent(querySnapshot: QuerySnapshot?, exception: FirebaseFirestoreException?) {
        if (querySnapshot != null && !querySnapshot.isEmpty) {
            value = querySnapshot
        }
    }
}
