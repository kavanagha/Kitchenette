package com.kithcenette.kitchenette_v2

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.BitmapFactory
import android.widget.Toast
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper
import java.util.ArrayList

const val DATABASE_NAME = "kitchenette.db"

const val TABLE_FOOD = "food"
const val COL_FOOD_ID = "id"
const val COL_FOOD_NAME = "name"
const val COL_FOOD_CATEGORY = "category"
const val COL_FOOD_CUPBOARD = "cupboard"
const val COL_FOOD_FAVOURITE = "favourite"
const val COL_FOOD_SHOPPING = "shoppingList"
const val COL_FOOD_QUANTITY = "quantity"
const val COL_FOOD_MEASUREMENT = "measurement"
const val COL_FOOD_BOUGHT = "bought"
const val COL_FOOD_PHOTO = "photo"

const val TABLE_BARCODE = "barcodes"
const val COL_BARCODE_ID = "id"
const val COL_BARCODE_BARCODE = "barcode"
const val COL_BARCODE_TYPE = "type"
const val COL_BARCODE_FOODID = "foodID"
const val COL_BARCODE_BRAND = "brand"
const val COL_BARCODE_QUANTITY = "quantity"
const val COL_BARCODE_MEASUREMENT = "measurement"

const val TABLE_RECIPE = "recipes"
const val COL_RECIPE_ID = "id"
const val COL_RECIPE_NAME = "name"
const val COL_RECIPE_MEAL = "mealType"
const val COL_RECIPE_CUISINE = "cuisine"
const val COL_RECIPE_DESCRIPTION = "description"
const val COL_RECIPE_METHOD = "method"
const val COL_RECIPE_FAVOURITE = "favourite"
const val COL_RECIPE_PHOTO =  "photo"
const val COL_RECIPE_SERVINGS = "servings"

const val TABLE_INGREDIENT = "ingredients"
const val COL_INGREDIENT_ID = "id"
const val COL_INGREDIENT_RECIPE = "recipeID"
const val COL_INGREDIENT_FOOD = "foodID"
const val COL_INGREDIENT_QUANTITY = "quantity"
const val COL_INGREDIENT_MEASUREMENT = "measurement"

const val TABLE_DIET = "diet"
const val COL_DIET_ID = "id"
const val COL_DIET_NAME = "name"
const val COL_DIET_RECIPE = "recipeID"

class DataBaseHandler (var context: Context) : SQLiteAssetHelper(context, DATABASE_NAME, null, 1){

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    ///////////////// FOOD TABLE /////////////////

    fun insertFood(food: Food) : Long? {
        val db = this.writableDatabase

        val cv = ContentValues()
        cv.put(COL_FOOD_NAME, food.name)
        cv.put(COL_FOOD_CATEGORY, food.category)

        val result = db.insert(TABLE_FOOD,null,cv)
        return if(result == (-1).toLong()) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
            null
        } else {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
            result
        }
    }
    fun readFoodData() : MutableList<Food>{
        val list : MutableList<Food> = ArrayList()

        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_FOOD ORDER BY $COL_FOOD_NAME"
        val result = db.rawQuery(query, null)
        if(result.moveToFirst()){
            do {
                val food = Food()
                food.id = result.getString(result.getColumnIndex(COL_FOOD_ID)).toInt()
                food.name = result.getString(result.getColumnIndex(COL_FOOD_NAME))
                list.add(food)
            }while (result.moveToNext())
        }
        result.close()
        db.close()
        return list
    }
    fun findFood(id : Int) : Food? {
        val db = this.readableDatabase

        val query = "SELECT * FROM $TABLE_FOOD WHERE $COL_FOOD_ID = ?"
        db.rawQuery(query, arrayOf(id.toString())).use{
                if (it.moveToFirst()){
                    val food = Food()
                    food.name=it.getString(it.getColumnIndex(COL_FOOD_NAME))
                    food.category=it.getString(it.getColumnIndex(COL_FOOD_CATEGORY))
                    food.shoppingList=it.getString(it.getColumnIndex(COL_FOOD_SHOPPING)).toInt()
                    val image = it.getBlob(it.getColumnIndex(COL_FOOD_PHOTO))
                    if (image!=null)
                        food.photo = BitmapFactory.decodeByteArray(image, 0, image.size )
                    return food
                }
        }
        db.close()
        return null
    }
    fun findFoodName(name:String): Int? {
        val db = this.readableDatabase

        val query = "SELECT * FROM $TABLE_FOOD WHERE $COL_FOOD_NAME = ?"
        db.rawQuery(query, arrayOf(name)).use{
            if (it.moveToFirst()){
                val food = Food()
                food.id=it.getString(it.getColumnIndex(COL_FOOD_ID)).toInt()
                val id = food.id
                return id
            }
        }
        db.close()
        return null
    }

    fun addFoodShopping(id:Int) {
        val db = this.writableDatabase

        val cv = ContentValues()
        cv.put(COL_FOOD_SHOPPING, "1")

        val result = db.update(TABLE_FOOD, cv, "$COL_FOOD_ID = $id", null)
        if(result >=1 ) {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        }
        db.close()
    }
    fun removeFoodShopping(id:Int){
        val db = this.writableDatabase

        val cv = ContentValues()
        cv.put(COL_FOOD_SHOPPING, "0")

        val result = db.update(TABLE_FOOD, cv, "$COL_FOOD_ID = $id", null)
        if(result >=1 ) {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        }
        db.close()
    }
    fun readShopping():MutableList<Food>{
        val list : MutableList<Food> = ArrayList()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_FOOD WHERE $COL_FOOD_SHOPPING =\"1\" " +
                "ORDER BY $COL_FOOD_CATEGORY, $COL_FOOD_NAME"
        val result = db.rawQuery(query, null)
        if(result.moveToFirst()){
            do {
                val food = Food()
                food.id = result.getString(result.getColumnIndex(COL_FOOD_ID)).toInt()
                list.add(food)
            }while (result.moveToNext())
        }
        result.close()
        db.close()
        return list
    }

    fun addFoodBought(id:Int){
        val db = this.writableDatabase

        val cv = ContentValues()
        cv.put(COL_FOOD_BOUGHT, "1")

        val result = db.update(TABLE_FOOD, cv, "$COL_FOOD_ID = $id", null)
        if(result >=1 ) {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        }
        db.close()
    }
    fun removeFoodBought(id:Int){
        val db = this.writableDatabase

        val cv = ContentValues()
        cv.put(COL_FOOD_BOUGHT, "0")

        val result = db.update(TABLE_FOOD, cv, "$COL_FOOD_ID = $id", null)
        if(result >=1 ) {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        }
        db.close()
    }
    fun readBought():MutableList<Food>{
        val list : MutableList<Food> = ArrayList()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_FOOD WHERE $COL_FOOD_BOUGHT =\"1\" ORDER BY $COL_FOOD_NAME"
        val result = db.rawQuery(query, null)
        if(result.moveToFirst()){
            do {
                val food = Food()
                food.id = result.getString(result.getColumnIndex(COL_FOOD_ID)).toInt()
                list.add(food)
            }while (result.moveToNext())
        }
        result.close()
        db.close()
        return list
    }

    fun addFoodFavourites(id:Int){
        val db = this.writableDatabase

        val cv = ContentValues()
        cv.put(COL_FOOD_FAVOURITE, "1")

        val result = db.update(TABLE_FOOD, cv, "$COL_FOOD_ID = $id", null)
        if(result >=1 ) {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        }
        db.close()
    }
    fun removeFoodFavourites(id:Int){
        val db = this.writableDatabase

        val cv = ContentValues()
        cv.put(COL_FOOD_SHOPPING, "0")

        val result = db.update(TABLE_FOOD, cv, "$COL_FOOD_ID = $id", null)
        if(result >=1 ) {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        }
        db.close()
    }
    fun readFoodFavourites():MutableList<Food>{
        val list : MutableList<Food> = ArrayList()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_FOOD WHERE $COL_FOOD_FAVOURITE =\"1\" ORDER BY $COL_FOOD_NAME"
        val result = db.rawQuery(query, null)
        if(result.moveToFirst()){
            do {
                val food = Food()
                food.id = result.getString(result.getColumnIndex(COL_FOOD_ID)).toInt()
                list.add(food)
            }while (result.moveToNext())
        }
        result.close()
        db.close()
        return list
    }

    fun readFoodCategory(cat:String):MutableList<Food>{
        val list : MutableList<Food> = ArrayList()
        val db = this.readableDatabase

        val query = "SELECT * FROM $TABLE_FOOD WHERE $COL_FOOD_CATEGORY = \"$cat\" ORDER BY $COL_FOOD_NAME"

        val result = db.rawQuery(query, null)
        if(result.moveToFirst()){
            do {
                val food = Food()
                food.id = result.getString(result.getColumnIndex(COL_FOOD_ID)).toInt()
                list.add(food)
            }while (result.moveToNext())
        }
        result.close()
        db.close()
        return list
    }

    fun addFoodCupboard(id:Int){
        val db = this.writableDatabase

        val cv = ContentValues()
        cv.put(COL_FOOD_CUPBOARD, "1")

        val result = db.update(TABLE_FOOD, cv, "$COL_FOOD_ID = $id", null)
        if(result >=1 ) {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        }
        db.close()
    }
    fun removeFoodCupboard(id:Int){
        val db = this.writableDatabase

        val cv = ContentValues()
        cv.put(COL_FOOD_CUPBOARD, "0")

        val result = db.update(TABLE_FOOD, cv, "$COL_FOOD_ID = $id", null)
        if(result >=1 ) {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        }
        db.close()
    }
    fun readFoodCupboard() :MutableList<Food>{
        val list : MutableList<Food> = ArrayList()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_FOOD WHERE $COL_FOOD_CUPBOARD =\"1\" " +
                "ORDER BY $COL_FOOD_CATEGORY, $COL_FOOD_NAME"
        val result = db.rawQuery(query, null)
        if(result.moveToFirst()){
            do {
                val food = Food()
                food.id = result.getString(result.getColumnIndex(COL_FOOD_ID)).toInt()
                list.add(food)
            }while (result.moveToNext())
        }
        result.close()
        db.close()
        return list
    }

    fun findFoodQuantity(id : Int) : Food? {
        val db = this.readableDatabase

        val query = "SELECT * FROM $TABLE_FOOD WHERE $COL_FOOD_ID = ?"
        db.rawQuery(query, arrayOf(id.toString())).use{
            if (it.moveToFirst()){
                val food = Food()
                food.name=it.getString(it.getColumnIndex(COL_FOOD_NAME))
                food.category=it.getString(it.getColumnIndex(COL_FOOD_CATEGORY))
                food.quantity = it.getString(it.getColumnIndex(COL_FOOD_QUANTITY)).toInt()
                food.measurement = it.getString(it.getColumnIndex(COL_FOOD_MEASUREMENT))
                return food
            }
        }
        db.close()
        return null
    }
    fun addFoodQuantity(id:Int, qty:Int, msr:String){
        val db = this.writableDatabase
        val cv = ContentValues()

        val food : Food? = findFoodQuantity(id)

        val old_qty : Int = food?.quantity!!.toInt()
        val new_qty : Int = old_qty + qty

        cv.put(COL_FOOD_QUANTITY,new_qty)
        cv.put(COL_FOOD_MEASUREMENT,msr)

        val result = db.update(TABLE_FOOD, cv, "$COL_FOOD_ID = $id", null)
        if(result >=1 ) {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        }
        db.close()
    }

    /****************** RECIPES TABLE *******************/
    fun readRecipeData() : MutableList<Recipe>{
        val list : MutableList<Recipe> = ArrayList()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_RECIPE ORDER BY $COL_RECIPE_NAME"
        val result = db.rawQuery(query, null)
        if(result.moveToFirst()){
            do {
                val recipe = Recipe()
                recipe.id = result.getString(result.getColumnIndex(COL_RECIPE_ID)).toInt()
                recipe.name = result.getString(result.getColumnIndex(COL_RECIPE_NAME))
                list.add(recipe)
            }while (result.moveToNext())
        }
        result.close()
        db.close()
        return list
    }
    fun findRecipe(id:Int): Recipe?{
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_RECIPE WHERE $COL_RECIPE_ID = ?"
        db.rawQuery(query, arrayOf(id.toString())).use{
            if (it.moveToFirst()){
                val recipe = Recipe()
                recipe.name=it.getString(it.getColumnIndex(COL_RECIPE_NAME))
                recipe.mealType = it.getString(it.getColumnIndex(COL_RECIPE_MEAL))
                recipe.cuisine = it.getString(it.getColumnIndex(COL_RECIPE_CUISINE))
                recipe.description = it.getString(it.getColumnIndex(COL_RECIPE_DESCRIPTION))
                recipe.method = it.getString(it.getColumnIndex(COL_RECIPE_METHOD))
                recipe.favourite = it.getString(it.getColumnIndex(COL_RECIPE_FAVOURITE)).toInt()
                recipe.servings = it.getString(it.getColumnIndex(COL_RECIPE_SERVINGS)).toInt()
                val image = it.getBlob(it.getColumnIndex(COL_RECIPE_PHOTO))
                if (image!=null)
                    recipe.photo = BitmapFactory.decodeByteArray(image, 0, image.size )
                return recipe
            }
        }
        db.close()
        return null
    }

    /************************ INGREDIENTS TABLE ********************************/
    fun findIngredients(id:Int) : Ingredients?{
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_RECIPE WHERE $COL_RECIPE_ID = ?"
        db.rawQuery(query, arrayOf(id.toString())).use{
            if (it.moveToFirst()) {
                val ingredient = Ingredients()

                return ingredient
            }
        }
        db.close()
        return null
    }

    //////// BARCODE TABLE ////////////////

    fun insertBarcode(barcode: Barcodes){
        val db = this.writableDatabase

        val cv = ContentValues()
        cv.put(COL_BARCODE_BARCODE, barcode.barcode)

        val result = db.insert(TABLE_BARCODE,null,cv)
        if(result == (-1).toLong()) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        }
        else {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
        }
    }

    fun checkBarcode(barcode:Int) : Boolean{

        val db = this.readableDatabase

        val query = "SELECT * FROM " + TABLE_BARCODE + " WHERE " +
                COL_BARCODE_BARCODE + " = ?"

        db.rawQuery(query, arrayOf(barcode.toString())).use{
            if(it.count > 0)
                return true
        }
        db.close()
        return false
    }

    fun readBarcodeData(): MutableList<Barcodes>{
        val list : MutableList<Barcodes> = ArrayList()

        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_BARCODE"
        val result = db.rawQuery(query, null)
        if(result.moveToFirst()){
            do {
                val barcode = Barcodes()
                barcode.id = result.getString(result.getColumnIndex(COL_BARCODE_ID)).toInt()
                barcode.barcode = result.getString(result.getColumnIndex(COL_BARCODE_BARCODE)).toInt()
                list.add(barcode)
            }while (result.moveToNext())
        }
        result.close()
        db.close()
        return list
    }
}