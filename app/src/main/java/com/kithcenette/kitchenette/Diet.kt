package com.kithcenette.kitchenette

class Diet{
    var id: Int = 0
    var name: String = ""
    var recipeID: Int = 0


    constructor(name:String, recipeID:Int){
        this.name = name
        this.recipeID=recipeID
    }

    constructor(){}
}