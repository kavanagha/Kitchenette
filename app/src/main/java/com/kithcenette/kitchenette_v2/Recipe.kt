package com.kithcenette.kitchenette_v2

import android.graphics.Bitmap

class Recipe{
    var id: Int = 0
    var name: String = ""
    var mealType: String = ""
    var cuisine: String = ""
    //var diet: Boolean = false
    var description: String = ""
    var method:String = ""
    var favourite: Int = 0
    var photo: Bitmap? = null


    constructor(name:String, mealType:String, cuisine:String,
                description:String, method:String){
        this.name = name
        this.mealType = mealType
        this.cuisine = cuisine
       // this.diet = diet
        this.description = description
        this.method = method
      //  this.favourite = favourite
    }

    constructor(){}
}