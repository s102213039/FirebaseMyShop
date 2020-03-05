package com.yanli.myshop.model

data class Item(var title: String, var price: Int) {
    constructor() : this("", 0)
}