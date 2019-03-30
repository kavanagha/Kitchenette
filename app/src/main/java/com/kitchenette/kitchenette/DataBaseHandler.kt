package com.kitchenette.kitchenette

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
import android.widget.Toast
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper
import java.util.ArrayList
import android.graphics.Bitmap
import java.io.ByteArrayOutputStream


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
const val COL_BARCODE_SCANNED = "scanned"
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

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {    }

    /****************** FOOD TABLE *******************/

    fun insertFood(food: Food) : Long? {
        val db = this.writableDatabase

        val bos = ByteArrayOutputStream()
        food.photo?.compress(Bitmap.CompressFormat.JPEG, 10, bos)
        val bArray = bos.toByteArray()

        val cv = ContentValues()
        cv.put(COL_FOOD_NAME, food.name)
        cv.put(COL_FOOD_CATEGORY, food.category)
        cv.put(COL_FOOD_PHOTO, bArray)

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
                    food.favourite=it.getString(it.getColumnIndex(COL_FOOD_FAVOURITE)).toInt()
                    food.quantity = it.getString(it.getColumnIndex(COL_FOOD_QUANTITY)).toDouble()
                    food.measurement = it.getString(it.getColumnIndex(COL_FOOD_MEASUREMENT))
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
                return food.id
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
        cv.put(COL_FOOD_FAVOURITE, "0")

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
    private fun removeFoodCupboard(id:Int){
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
                food.name = result.getString(result.getColumnIndex(COL_FOOD_NAME))
                list.add(food)
            }while (result.moveToNext())
        }
        result.close()
        db.close()
        return list
    }

    private fun findFoodQuantity(id : Int) : Food? {
        val db = this.readableDatabase

        val query = "SELECT * FROM $TABLE_FOOD WHERE $COL_FOOD_ID = ?"
        db.rawQuery(query, arrayOf(id.toString())).use{
            if (it.moveToFirst()){
                val food = Food()
                food.name=it.getString(it.getColumnIndex(COL_FOOD_NAME))
                food.category=it.getString(it.getColumnIndex(COL_FOOD_CATEGORY))
                food.quantity = it.getString(it.getColumnIndex(COL_FOOD_QUANTITY)).toDouble()
                food.measurement = it.getString(it.getColumnIndex(COL_FOOD_MEASUREMENT))
                return food
            }
        }
        db.close()
        return null
    }
    fun addFoodQuantity(id:Int, qty:Double, msr:String){
        val db = this.writableDatabase
        val cv = ContentValues()

        val food : Food? = findFoodQuantity(id)
        val m : String? = findMeasurement(id)

        val oldqty : Double = food?.quantity!!.toDouble()
        val oldG = Measurements(oldqty, m!!, "grams")
        oldG.convert()
        val newG = Measurements(qty, msr, "grams")
        newG.convert()
        val newqty : Double = oldG.quantity + newG.quantity
        val resultQty = Measurements(newqty, "grams", m)
        resultQty.convert()

        cv.put(COL_FOOD_QUANTITY,resultQty.quantity)
        val result = db.update(TABLE_FOOD, cv, "$COL_FOOD_ID = $id", null)
        if(result >=1 ) {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        }
        addFoodCupboard(id)
        db.close()
    }
    fun delFoodQuantity(id:Int, qty:Double, msr:String){
        val db = this.writableDatabase
        val cv = ContentValues()

        val food : Food? = findFoodQuantity(id)
        val m : String? = findMeasurement(id)

        val oldqty : Double = food?.quantity!!
        val oldG = Measurements(oldqty, m!!, "grams")
        oldG.convert()
        val newG = Measurements(qty, msr, "grams")
        newG.convert()
        val newqty : Double = oldG.quantity - newG.quantity
        val resultQty = Measurements(newqty, "grams", m)
        resultQty.convert()

        if (resultQty.quantity <= 0.0) {
            setFoodQuantity(id,0.0)
            removeFoodCupboard(id)
        }
        else{
            cv.put(COL_FOOD_QUANTITY,resultQty.quantity)
            db.update(TABLE_FOOD, cv, "$COL_FOOD_ID = $id", null)
        }
        db.close()
    }
    private fun checkDelFoodQuantity(id:Int, qty:Double, msr:String) : Boolean{
        val food : Food? = findFoodQuantity(id)
        val m : String? = findMeasurement(id)

        val oldqty : Double = food?.quantity!!
        val oldG = Measurements(oldqty, m!!, "grams")
        oldG.convert()
        val newG = Measurements(qty, msr, "grams")
        newG.convert()
        val newqty : Double = oldG.quantity - newG.quantity
        val resultQty = Measurements(newqty, "grams", m)
        val convert = resultQty.convert()

        return resultQty.quantity >= 0.0
    }

    private fun setFoodQuantity(id:Int, qty:Double){
        val db = this.writableDatabase

        val cv = ContentValues()
        cv.put(COL_FOOD_QUANTITY, qty)

        val result = db.update(TABLE_FOOD, cv, "$COL_FOOD_ID = $id", null)
        if(result >=1 ) {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        }
        db.close()
    }
    private fun findMeasurement(id : Int) : String?{
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_FOOD WHERE $COL_FOOD_ID = ?"
        db.rawQuery(query, arrayOf(id.toString())).use{
            if (it.moveToFirst()){
                return it.getString(it.getColumnIndex(COL_FOOD_MEASUREMENT))
            }
        }
        db.close()
        return null
    }


    /****************** RECIPES TABLE *******************/
    fun insertRecipe(recipe:Recipe) : Long?{
        val db = this.writableDatabase
        val bos = ByteArrayOutputStream()

        recipe.photo?.compress(Bitmap.CompressFormat.JPEG, 10, bos)
        val bArray = bos.toByteArray()
        val cv = ContentValues()

        cv.put(COL_RECIPE_NAME, recipe.name)
        cv.put(COL_RECIPE_METHOD,recipe.method)
        cv.put(COL_RECIPE_CUISINE, recipe.cuisine)
        cv.put(COL_RECIPE_DESCRIPTION, recipe.description)
        cv.put(COL_RECIPE_MEAL, recipe.mealType)
        cv.put(COL_RECIPE_PHOTO, bArray)
        cv.put(COL_RECIPE_SERVINGS, recipe.servings)

        val result = db.insert(TABLE_RECIPE, null, cv)
        return if(result == (-1).toLong()) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
            null
        } else {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
            result
        }
    }

    fun readRecipeData() : MutableList<Recipe>{
        val list : MutableList<Recipe> = ArrayList()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_RECIPE ORDER BY $COL_RECIPE_FAVOURITE, $COL_RECIPE_NAME"
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

    fun addRecipeFavourites(id:Int){
        val db = this.writableDatabase

        val cv = ContentValues()
        cv.put(COL_RECIPE_FAVOURITE, "1")

        val result = db.update(TABLE_RECIPE, cv, "$COL_RECIPE_ID = $id", null)
        if(result >=1 ) {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        }
        db.close()
    }
    fun removeRecipeFavourites(id:Int){
        val db = this.writableDatabase

        val cv = ContentValues()
        cv.put(COL_RECIPE_FAVOURITE, "0")

        val result = db.update(TABLE_RECIPE, cv, "$COL_RECIPE_ID = $id", null)
        if(result >=1 ) {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        }
        db.close()
    }
    fun readRecipeFavourites():MutableList<Recipe>{
        val list : MutableList<Recipe> = ArrayList()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_RECIPE WHERE $COL_RECIPE_FAVOURITE =\"1\" ORDER BY $COL_FOOD_NAME"
        val result = db.rawQuery(query, null)
        if(result.moveToFirst()){
            do {
                val recipe = Recipe()
                recipe.id = result.getString(result.getColumnIndex(COL_RECIPE_ID)).toInt()
                list.add(recipe)
            }while (result.moveToNext())
        }
        result.close()
        db.close()
        return list
    }

    fun suggestRecipes():MutableList<Recipe>{
        val cupboard : MutableList<Food> = readFoodCupboard()
        val suggested : MutableList<Recipe> = ArrayList()
        val recipes : MutableList<Recipe> = readRecipeData()

        for(i in 0..(recipes.size-1)){
            val recipe : Recipe = recipes[i]
            val ingredientList : MutableList<Ingredients> = readIngredients(recipe.id)
            var valid = false
            for( j in 0..(ingredientList.size-1)){
                val ingredients : Ingredients =ingredientList[j]
                var found = false
                for (x in 0..(cupboard.size-1)){
                    if (ingredients.foodID == cupboard[x].id){
                        if(checkDelFoodQuantity(ingredients.foodID, ingredients.quantity, ingredients.measurement)) {
                            found = true
                            break
                        }
                    }
                }
                if(!found){
                   valid = false
                    break
                }
                else
                    valid = true
            }
            if(valid)
                suggested.add(recipe)
        }
        return suggested
    }

    /************************ INGREDIENTS TABLE ********************************/
    fun insertIngredient(ingredients: Ingredients){
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(COL_INGREDIENT_RECIPE, ingredients.recipeID)
        cv.put(COL_INGREDIENT_FOOD, ingredients.foodID)
        cv.put(COL_INGREDIENT_QUANTITY, ingredients.quantity)
        cv.put(COL_INGREDIENT_MEASUREMENT, ingredients.measurement)

        db.insert(TABLE_INGREDIENT, null, cv)
    }
    fun readIngredients(id:Int) : MutableList<Ingredients>{
        val list : MutableList<Ingredients> = ArrayList()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_INGREDIENT WHERE $COL_INGREDIENT_RECIPE = ?"
        val result = db.rawQuery(query, arrayOf(id.toString()))
        if (result.moveToFirst()) {
            do {
                val ingredient = Ingredients()
                ingredient.id = result.getString(result.getColumnIndex(COL_INGREDIENT_ID)).toInt()
                ingredient.foodID = result.getString(result.getColumnIndex(COL_INGREDIENT_FOOD)).toInt()
                list.add(ingredient)
            }while (result.moveToNext())
        }
        result.close()
        db.close()
        return list
    }
    fun findIngredient(id:Int) : Ingredients?{
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_INGREDIENT WHERE $COL_INGREDIENT_ID = ?"
        db.rawQuery(query, arrayOf(id.toString())).use{
            if (it.moveToFirst()){
                val ingredient = Ingredients()
                ingredient.quantity = it.getString(it.getColumnIndex(COL_INGREDIENT_QUANTITY)).toDouble()
                ingredient.measurement = it.getString(it.getColumnIndex(COL_INGREDIENT_MEASUREMENT))
                return ingredient
            }
        }
        db.close()
        return null
    }

    fun removeQuantityCupboard(iId:Int, fId:Int){
        val db = this.readableDatabase
        val ingredient = findIngredient((iId))

        val iQuantity = ingredient!!.quantity
        val msr : String? = ingredient.measurement
        delFoodQuantity(fId, iQuantity, msr!!)

        db.close()
    }

    /************************ DIET TABLE ********************************/
    fun insertDiet(diet: Diet){
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(COL_DIET_NAME, diet.name)
        cv.put(COL_DIET_RECIPE, diet.recipeID)

        db.insert(TABLE_DIET, null, cv)
    }

    /************************** BARCODE TABLE **************************************/

    fun insertBarcode(barcode: Barcodes){
        val db = this.writableDatabase

        val cv = ContentValues()
        cv.put(COL_BARCODE_BARCODE, barcode.barcode)
        cv.put(COL_BARCODE_SCANNED, System.currentTimeMillis())

        val result = db.insert(TABLE_BARCODE,null,cv)
        if(result == (-1).toLong()) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        }
        else {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
        }
    }
    fun findBarcode(id:Int):Barcodes?{
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_BARCODE WHERE $COL_BARCODE_ID = ?"
        db.rawQuery(query, arrayOf(id.toString())).use{
            if (it.moveToFirst()){
                val barcode = Barcodes()
                barcode.barcode = it.getString(it.getColumnIndex(COL_BARCODE_BARCODE))
                barcode.type = it.getString(it.getColumnIndex(COL_BARCODE_TYPE))
                barcode.brand = it.getString(it.getColumnIndex(COL_BARCODE_BRAND))
                if(it.getString(it.getColumnIndex(COL_BARCODE_FOODID)) != null)
                    barcode.foodID = it.getString(it.getColumnIndex(COL_BARCODE_FOODID)).toInt()
                else
                    barcode.foodID = null
                barcode.quantity = it.getString(it.getColumnIndex(COL_BARCODE_QUANTITY)).toDouble()
                barcode.measurement = it.getString(it.getColumnIndex(COL_BARCODE_MEASUREMENT))
                return barcode
            }
        }
        db.close()
        return null
    }
    fun checkBarcode(barcode:String) : Boolean{
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
    fun updateLastScan(barcode : String){
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COL_BARCODE_SCANNED,System.currentTimeMillis())
        val result = db.update(TABLE_BARCODE, cv, "$COL_BARCODE_BARCODE = $barcode", null)
        if(result >=1 ) {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        }
        db.close()
    }
    fun updateBarcode(bId : Int, fID : Int, qty : Double, msr : String, brand : String){
        val db = this.writableDatabase
        val cv = ContentValues()

        cv.put(COL_BARCODE_FOODID,fID)
        cv.put(COL_BARCODE_QUANTITY, qty)
        cv.put(COL_BARCODE_MEASUREMENT, msr)
        cv.put(COL_BARCODE_BRAND, brand)

        val result = db.update(TABLE_BARCODE, cv, "$COL_BARCODE_ID = $bId", null)
        if(result >=1 ) {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        }
        db.close()
    }
    fun findBarcodeName(barcode : String) : Int?{
        val db = this.readableDatabase

        val query = "SELECT * FROM " + TABLE_BARCODE + " WHERE " +
                COL_BARCODE_BARCODE + " = ?"

        db.rawQuery(query, arrayOf(barcode)).use{
            if (it.moveToFirst()){
                val barcodes = Barcodes()
                barcodes.id=it.getString(it.getColumnIndex(COL_BARCODE_ID)).toInt()
                return barcodes.id
            }
        }
        db.close()
        return null
    }
    fun readBarcodeData(): MutableList<Barcodes>{
        val list : MutableList<Barcodes> = ArrayList()

        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_BARCODE ORDER BY $COL_BARCODE_SCANNED DESC"
        val result = db.rawQuery(query, null)
        if(result.moveToFirst()){
            do {
                val barcode = Barcodes()
                barcode.id = result.getString(result.getColumnIndex(COL_BARCODE_ID)).toInt()
                barcode.barcode = result.getString(result.getColumnIndex(COL_BARCODE_BARCODE))
                list.add(barcode)
            }while (result.moveToNext())
        }
        result.close()
        db.close()
        return list
    }
}