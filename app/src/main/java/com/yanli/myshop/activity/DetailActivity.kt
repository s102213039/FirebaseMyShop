package com.yanli.myshop.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.yanli.myshop.R
import com.yanli.myshop.model.Item
import com.yanli.myshop.model.WatchItem
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    private lateinit var item: Item

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        intent.getParcelableExtra<Item>("item")?.let {
            item = it
        }

        web.settings.javaScriptEnabled = true
        web.settings.loadWithOverviewMode = true
        web.settings.useWideViewPort = true
        web.loadUrl(item.content)

        //get watchItem from Firebase
        FirebaseFirestore.getInstance().collection("users")
            .document(uid!!)
            .collection("watchItems")
            .document(item.id).get()
            .addOnCompleteListener {
                if(it.isSuccessful){
                    val watchItem = it.result?.toObject(WatchItem::class.java)
                    watchItem?.let {
                        watch.isChecked = true
                    }
                }
            }

        //watches
        watch.setOnCheckedChangeListener { button, checked ->
            if (checked) {
                FirebaseFirestore.getInstance().collection("users")
                    .document(uid!!)
                    .collection("watchItems")
                    .document(item.id)
                    .set(WatchItem(item.id))
            }else{
                FirebaseFirestore.getInstance().collection("users")
                    .document(uid!!)
                    .collection("watchItems")
                    .document(item.id)
                    .delete()
            }

        }
    }

    override fun onStart() {
        super.onStart()
        item.viewCount++
        item.id.let {
            FirebaseFirestore.getInstance().collection("items")
                .document(it).update("viewCount", item.viewCount)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}
