package com.kithcenette.kitchenette_v2

class Barcodes{
    var id:Int =0
    var barcode:Int = 0
    var type:String = ""
    var foodID:Int = 0
    var brand:String = ""
    var quantity:Int = 0
    var measurement: String = ""

    constructor(barcode:Int){
        this.barcode= barcode
    }

    constructor(){}

}