package com.kitchenette.kitchenette

class Barcodes{
    var id:Int =0
    var barcode:String = ""
    var type:String = ""
    var foodID: Int? = null
    var brand:String = ""
    var quantity:Double = 0.0
    var measurement: String = ""

    constructor(barcode:String){
        this.barcode= barcode
    }

    constructor(){}

}