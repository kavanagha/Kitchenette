package com.kitchenette.kitchenette

class Diet{
    var id: Int = 0
    var name: String = ""
    var recipeID: Long = 0


    constructor(name:String, recipeID:Long){
        this.name = name
        this.recipeID=recipeID
    }

    constructor(){}
}