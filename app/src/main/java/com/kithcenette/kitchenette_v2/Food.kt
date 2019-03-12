package com.kithcenette.kitchenette_v2

import android.graphics.Bitmap

class Food {
    var id: Int = 0
    var name: String = ""
    var category: String = ""
    var cupboard: Int = 0 //bool
    var favourite: Int = 0  //bool
    var shoppingList: Int = 0   //bool
    var quantity: Int = 0
    var measurement: String = ""
    var bought: Int = 0 //bool
    var photo: Bitmap? = null

    constructor(name:String, category:String){
        this.name = name
        this.category = category

    }

    constructor(){}

}