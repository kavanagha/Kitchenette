package com.kithcenette.kitchenette_v2

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import java.util.ArrayList

const val DATABASE_NAME = "Kitchenette"

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

const val TABLE_BARCODE = "barcodes"
const val COL_BARCODE_ID = "id"
const val COL_BARCODE_BARCODE = "barcode"
const val COL_BARCODE_TYPE = "type"
const val COL_BARCODE_FOODID = "foodID"
const val COL_BARCODE_BRAND = "brand"
const val COL_BARCODE_QUANTITY = "quantity"
const val COL_BARCODE_MEASUREMENT = "measurement"


class DataBaseHandler (var context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1){

    override fun onCreate(db: SQLiteDatabase?) {
        val createFoodTable = "CREATE TABLE " + TABLE_FOOD + " (" +
                COL_FOOD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_FOOD_NAME + " varchar(256),  " +
                COL_FOOD_CATEGORY + " varchar(256), " +
                COL_FOOD_CUPBOARD + " INTEGER DEFAULT 0, " +
                COL_FOOD_FAVOURITE  + " INTEGER DEFAULT 0, " +
                COL_FOOD_SHOPPING + " INTEGER DEFAULT 0, " +
                COL_FOOD_QUANTITY + " INTEGER, "  +
                COL_FOOD_MEASUREMENT + " varchar(256), " +
                COL_FOOD_BOUGHT + " INTEGER DEFAULT 0)";
        db?.execSQL(createFoodTable)

        val createBarcodeTable = "CREATE TABLE " + TABLE_BARCODE + " ("+
                COL_BARCODE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_BARCODE_BARCODE + " INTEGER, " +
                COL_BARCODE_FOODID + " INTEGER, " +
                COL_BARCODE_BRAND + " VARCHAR(256), " +
                COL_BARCODE_QUANTITY + " INTEGER, " +
                COL_BARCODE_MEASUREMENT + " VARCHAR(256) )";
        db?.execSQL(createBarcodeTable)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /////// FOOD TABLE //////////////

    fun insertFood(food: Food) : Long? {
        val db = this.writableDatabase

        var cv = ContentValues()
        cv.put(COL_FOOD_NAME, food.name)
        cv.put(COL_FOOD_CATEGORY, food.category)

        var result = db.insert(TABLE_FOOD,null,cv)
        return if(result == (-1).toLong()) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
            null
        } else {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
            result
        }
    }

    fun readFoodData() : MutableList<Food>{
        var list : MutableList<Food> = ArrayList()

        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_FOOD"
        val result = db.rawQuery(query, null)
        if(result.moveToFirst()){
            do {
                var food = Food()
                food.id = result.getString(result.getColumnIndex(COL_FOOD_ID)).toInt()
                food.name = result.getString(result.getColumnIndex(COL_FOOD_NAME))
                //food.quantity = result.getString(result.getColumnIndex(COL_FOOD_QUANTITY)).toInt()
                //food.measurement = result.getString(result.getColumnIndex(COL_FOOD_MEASUREMENT))
                list.add(food)
            }while (result.moveToNext())
        }
        result.close()
        db.close()
        return list
    }

    fun findFood(id : Int) : Food?
    {
        val db = this.readableDatabase

        val query = "SELECT * FROM " + TABLE_FOOD + " WHERE " +
                COL_FOOD_ID + " = ?"
        db.rawQuery(query, arrayOf(id.toString())).use{
                if (it.moveToFirst()){
                        val food = Food()
                        food.name=it.getString(it.getColumnIndex(COL_FOOD_NAME))
                        food.category=it.getString(it.getColumnIndex(COL_FOOD_CATEGORY))
                        return food
                }
        }
        db.close()
        return null
    }

    //////// BARCODE TABLE ////////////////

    fun insertBarcode(barcode: Barcodes){
        val db = this.writableDatabase

        var cv = ContentValues()
        cv.put(COL_BARCODE_BARCODE, barcode.barcode)

        var result = db.insert(TABLE_BARCODE,null,cv)
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
        var list : MutableList<Barcodes> = ArrayList()

        val db = this.readableDatabase
        val query = "SELECT * FROM " + TABLE_BARCODE
        val result = db.rawQuery(query, null)
        if(result.moveToFirst()){
            do {
                var barcode = Barcodes()
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