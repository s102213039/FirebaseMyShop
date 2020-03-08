package com.yanli.myshop.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.yanli.myshop.R
import com.yanli.myshop.model.Item
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.item_row.*

class DetailActivity : AppCompatActivity() {

    private lateinit var item: Item

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        intent.getParcelableExtra<Item>("item")?.let {
            item = it
        }

        web.settings.javaScriptEnabled = true
        web.settings.loadWithOverviewMode = true
        web.settings.useWideViewPort = true
        web.loadUrl(item.content)
    }

    override fun onStart() {
        super.onStart()
        item.viewCount++
        item.id?.let {
            FirebaseFirestore.getInstance().collection("items")
                .document(it).update("viewCount", item.viewCount)
        }
    }
}
