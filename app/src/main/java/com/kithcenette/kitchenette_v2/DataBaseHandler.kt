package com.kithcenette.kitchenette_v2

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import java.util.ArrayList

val DATABASE_NAME = "Kitchenette"

val TABLE_FOOD = "food"
val COL_FOOD_ID = "id"
val COL_FOOD_NAME = "name"
val COL_FOOD_CATEGORY = "category"
val COL_FOOD_CUPBOARD = "cupboard"
val COL_FOOD_FAVOURITE = "favourite"
val COL_FOOD_SHOPPING = "shoppingList"
val COL_FOOD_QUANTITY = "quantity"
val COL_FOOD_MEASUREMENT = "measurement"
val COL_FOOD_BOUGHT = "bought"


class DataBaseHandler (var context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, 1){

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = "CREATE TABLE " + TABLE_FOOD + " (" +
                COL_FOOD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_FOOD_NAME + " varchar(256),  " +
                COL_FOOD_CATEGORY + " varchar(256), " +
                COL_FOOD_CUPBOARD + " INTEGER DEFAULT 0, " +
                COL_FOOD_FAVOURITE  + " INTEGER DEFAULT 0, " +
                COL_FOOD_SHOPPING + " INTEGER DEFAULT 0, " +
                COL_FOOD_QUANTITY + " INTEGER, "  +
                COL_FOOD_MEASUREMENT + " varchar(256), " +
                COL_FOOD_BOUGHT + " INTEGER DEFAULT 0)";

        db?.execSQL(createTable)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun insertFood(food: Food) {
        val db = this.writableDatabase

        var cv = ContentValues()
        cv.put(COL_FOOD_NAME, food.name)
        cv.put(COL_FOOD_CATEGORY, food.category)
        cv.put(COL_FOOD_QUANTITY, food.quantity)
        cv.put(COL_FOOD_MEASUREMENT, food.measurement)
        var result = db.insert(TABLE_FOOD,null,cv)
        if(result == -1.toLong()) {
            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
        }
        else {
            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
        }
    }

    fun readData() : MutableList<Food>{
        var list : MutableList<Food> = ArrayList()

        val db = this.readableDatabase
        val query = "SELECT * FROM " + TABLE_FOOD
        val result = db.rawQuery(query, null)
        if(result.moveToFirst()){
            do {
                var food = Food()
                food.id = result.getString(result.getColumnIndex(COL_FOOD_ID)).toInt()
                food.name = result.getString(result.getColumnIndex(COL_FOOD_NAME))

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

}