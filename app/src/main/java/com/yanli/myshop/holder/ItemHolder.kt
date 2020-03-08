package com.yanli.myshop.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.yanli.myshop.model.Item
import kotlinx.android.synthetic.main.item_row.view.*

class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var titleText = itemView.item_title
    var priceText = itemView.item_price
    var image = itemView.item_image
    var viewCountText = itemView.item_view_count

    fun bindTo(item: Item) {
        titleText.text = item.title
        priceText.text = "$ ${item.price}"
        viewCountText.text = item.viewCount.toString()
        Glide.with(itemView.context)
            .load(item.imageUrl)
            .apply(RequestOptions().override(120))
            .into(image)
    }
}