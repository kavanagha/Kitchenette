package com.kithcenette.kitchenette

class Measurements{
    var quantity : Double = 0.0
    var type : String = ""
    var desired : String = ""

    private val cupToMl : Double = 250.0
    private val pintToMl : Double = 570.0
    private val litreToMl : Double = 1000.0
    private val flOzToMl : Double = 29.6
    private val tspToMl : Double = 5.0
    private val tbspToMl : Double = 15.0
    private val dessertspoonToMl : Double = 10.0

    private val mlToGrams : Double = 1.0
    private val ozToGrams : Double = 30.0
    private val kgToGrams : Double = 1000.0


    constructor(quantity:Double, type:String, desired:String){
        this.quantity = quantity
        this.type = type
        this.desired = desired
    }

    fun convert(): Double?{
        return when {
            this.desired == "litres" -> toLitres()
            this.desired == "kg" -> toKG()
            this.desired == "grams" -> convertGrams()
            this.desired == "ml" -> convertMl()
            this.desired == "whole" -> whole()
            else -> null
        }
    }

    private fun whole(): Double? {
        return this.quantity
    }

    private fun toKG(): Double? {
        this.quantity = this.convertGrams()!!
        this.quantity = this.quantity / kgToGrams
        this.type = "kg"
        return this.quantity
    }

    private fun toLitres(): Double? {
        this.quantity = this.convertGrams()!!
        this.quantity = this.quantity / litreToMl
        this.type = "litres"
        return this.quantity
    }


    private fun convertMl():Double?{
        return when {
            this.type == "cups" -> cupml(this.quantity)
            this.type == "pint" -> pintml(this.quantity)
            this.type == "litres" -> litreml(this.quantity)
            this.type == "tsp" -> tspml(this.quantity)
            this.type == "tbsp" -> tbspml(this.quantity)
            this.type == "fl. oz" -> flozml(this.quantity)
            this.type == "dessertspoon" -> dspml(this.quantity)
            this.type == "grams" -> gramsml(this.quantity)
            else -> null
        }
    }

    private fun gramsml(quantity: Double): Double? {
        this.quantity = quantity * mlToGrams
        this.type = "ml"
        return this.quantity
    }

    private fun convertGrams():Double?{
        return when {
            this.type == "ml" -> mlgrams(this.quantity)
            this.type == "oz" -> ozgrams(this.quantity)
            this.type == "kg" -> kggrams(this.quantity)
            else -> convertMl()
        }
    }

    private fun kggrams(quantity: Double): Double? {
        this.quantity = quantity * kgToGrams
        this.type = "grams"
        return this.quantity
    }

    private fun ozgrams(quantity: Double): Double? {
        this.quantity = quantity * ozToGrams
        this.type = "grams"
        return this.quantity
    }

    private fun mlgrams(quantity: Double): Double? {
        this.quantity = quantity * mlToGrams
        this.type = "grams"
        return this.quantity
    }

    private fun dspml(quantity: Double): Double? {
        this.quantity = quantity * dessertspoonToMl
        this.type = "ml"
        return this.quantity
    }

    private fun flozml(quantity: Double): Double? {
        this.quantity = quantity * flOzToMl
        this.type = "ml"
        return this.quantity
    }

    private fun tbspml(quantity: Double): Double? {
        this.quantity = quantity * tbspToMl
        this.type = "ml"
        return this.quantity
    }

    private fun tspml(quantity: Double): Double? {
        this.quantity = quantity * tspToMl
        this.type = "ml"
        return this.quantity
    }

    private fun litreml(quantity: Double): Double? {
        this.quantity = quantity * litreToMl
        this.type = "ml"
        return this.quantity
    }

    private fun pintml(quantity: Double): Double? {
        this.quantity = quantity * pintToMl
        this.type = "ml"
        return this.quantity
    }

    private fun cupml(quantity:Double):Double?{
        this.quantity = quantity * cupToMl
        this.type = "ml"
        return this.quantity
    }
}