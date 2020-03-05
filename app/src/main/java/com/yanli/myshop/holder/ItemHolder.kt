package com.yanli.myshop.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.yanli.myshop.model.Item
import kotlinx.android.synthetic.main.item_row.view.*

class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var titleText = itemView.item_title
    var priceText = itemView.item_price

    fun bindTo(item: Item) {
        titleText.text = item.title
        priceText.text = item.price.toString()
    }
}