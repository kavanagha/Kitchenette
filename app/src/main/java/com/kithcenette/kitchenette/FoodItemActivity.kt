package com.kithcenette.kitchenette

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_food_item.*
import kotlinx.android.synthetic.main.app_bar_food_item.*
import kotlinx.android.synthetic.main.content_food_item.*

import android.graphics.Color
import android.view.*
import android.widget.*


class FoodItemActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    AdapterView.OnItemSelectedListener {

    var list = arrayOf("cup","dessertspoon","fl. oz",
        "grams","kg","litres","ml","oz","pint","tbsp","tsp", "whole")
    var s : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_item)
        setSupportActionBar(toolbar)

        /*********************************** NAV DRAWER *****************************************/
        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        /*********************************** SET FOOD ITEM *****************************************/

        val context = this
        val db = DataBaseHandler(context)

        val foodMessage: String = intent.getStringExtra("food")
        val id = foodMessage.toInt()

        var food : Food? = db.findFood(foodMessage.toInt())

        foodName.text = food?.name
        foodItem_category.text = food?.category
        val bitmap: Bitmap? = food?.photo
        image.setImageBitmap(bitmap)
        old_qty.text = food?.quantity.toString()
        old_msr.text = food?.measurement

        if(food?.shoppingList==1)
            shoppingButton.setColorFilter(Color.argb(255, 0, 191, 255))
        else
            shoppingButton.setColorFilter(Color.argb(0, 0, 0, 0))

        if(food?.favourite==1)
            favButton.setColorFilter(Color.argb(255, 0, 191, 255))
        else
            favButton.setColorFilter(Color.argb(0, 0, 0, 0))

        /*********************************** BUTTONS *****************************************/

        shoppingButton.setOnClickListener {
            food = if(food?.shoppingList==0) {
                db.addFoodShopping(foodMessage.toInt())
                shoppingButton.setColorFilter(Color.argb(255, 0, 191, 255))
                db.findFood(foodMessage.toInt())
            } else {
                db.removeFoodShopping(foodMessage.toInt())
                shoppingButton.setColorFilter(Color.argb(0, 0, 0, 0))
                db.findFood(foodMessage.toInt())
            }
        }

        favButton.setOnClickListener {
            food = if(food?.favourite==0) {
                db.addFoodFavourites(foodMessage.toInt())
                favButton.setColorFilter(Color.argb(255, 0, 191, 255))
                db.findFood(foodMessage.toInt())
            } else {
                db.removeFoodFavourites(foodMessage.toInt())
                favButton.setColorFilter(Color.argb(0, 0, 0, 0))
                db.findFood(foodMessage.toInt())
            }
        }

        addCupboard.setOnClickListener {

            val window = PopupWindow(context)
            val view = layoutInflater.inflate(R.layout.popup_add_quantity,null)

            window.isFocusable = true
            window.isOutsideTouchable = true
            window.update()

            window.contentView = view

            val old_qty = view.findViewById<TextView>(R.id.old_qty)
            old_qty.text  = food?.quantity.toString()
            val old_msr = view.findViewById<TextView>(R.id.old_msr)
            old_msr.text  = food?.measurement

            val spinner = view.findViewById<Spinner>(R.id.enter_measurement)

            spinner!!.onItemSelectedListener = this
            val aa = ArrayAdapter(context, android.R.layout.simple_spinner_item, list)
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = aa

            val qty = view.findViewById<EditText>(R.id.enter_quantity)

            val add = view.findViewById<ImageButton>(R.id.add_qty_btn)
            add.setOnClickListener{
                if(qty.text.toString().isNotEmpty() &&
                    s!!.isNotEmpty()){
                    db.addFoodQuantity(id, qty.text.toString().toDouble(),s.toString())
                    db.addFoodCupboard(id)
                    this.recreate()
                    window.dismiss()
                }
            }
            window.showAtLocation(root_layout, Gravity.CENTER,0,0)
        }

        removeCupboard.setOnClickListener {
            val window = PopupWindow(context)
            val view = layoutInflater.inflate(R.layout.popup_remove_quantity,null)

            window.isFocusable = true
            window.isOutsideTouchable = true
            window.update()

            window.contentView = view

            val old_qty = view.findViewById<TextView>(R.id.old_qty)
            old_qty.text  = food?.quantity.toString()
            val old_msr = view.findViewById<TextView>(R.id.old_msr)
            old_msr.text  = food?.measurement

            val spinner = view.findViewById<Spinner>(R.id.enter_measurement)

            spinner!!.onItemSelectedListener = this
            val aa = ArrayAdapter(context, android.R.layout.simple_spinner_item, list)
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = aa

            val qty = view.findViewById<EditText>(R.id.enter_quantity)

            val add = view.findViewById<ImageButton>(R.id.add_qty_btn)
            add.setOnClickListener{
                if(qty.text.toString().isNotEmpty() &&
                    s!!.isNotEmpty()){
                    db.delFoodQuantity(id, qty.text.toString().toDouble(),s.toString())
                    this.recreate()
                    window.dismiss()
                }
            }
            window.showAtLocation(root_layout, Gravity.CENTER,0,0)
        }


    }

    /*********************************** NAVIGATION METHODS *****************************************/
    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.settings_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_cupboard -> {
                val menuIntent = Intent(this@FoodItemActivity, MainActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_cookbook -> {
                val menuIntent = Intent(this@FoodItemActivity, CookbookActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_shopping -> {
                val menuIntent = Intent(this@FoodItemActivity, ShoppingListActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_favourite -> {
                val menuIntent = Intent(this@FoodItemActivity, FavouritesActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_barcode -> {
                val menuIntent = Intent(this@FoodItemActivity, ScanBarcodeActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_share -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    /*********************************** SPINNER METHODS *****************************************/
    override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long) {
        s = list[position]
    }
    override fun onNothingSelected(arg0: AdapterView<*>) {    }

}
