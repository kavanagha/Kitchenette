package com.kithcenette.kitchenette_v2

class Barcodes{
    var id:Int =0
    var barcode:Int = 0
    var type:String = ""
    var foodID:Int = 0
    var brand:String = ""
    var quantity:Int = 0
    var measurement: String = ""

    constructor(barcode:Int, type:String, foodID:Int, brand:String,
                quantity:Int, measurement:String){
        this.barcode= barcode
        this.type=type
        this.foodID= foodID
        this.brand= brand
        this.quantity=quantity
        this.measurement=measurement
    }

    constructor(){}

}