package com.kithcenette.kitchenette

class Ingredients{
    var id: Int = 0
    var recipeID : Int = 0
    var foodID : Int = 0
    var quantity: Double = 0.0
    var measurement: String = ""

    constructor(recipeID:Int, foodID:Int, quantity:Double, measurement:String){
        this.recipeID=recipeID
        this.foodID=foodID
        this.quantity=quantity
        this.measurement=measurement
    }

    constructor(){}
}