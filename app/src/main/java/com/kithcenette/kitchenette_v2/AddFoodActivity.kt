package com.kithcenette.kitchenette_v2

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_add_food.*
import kotlinx.android.synthetic.main.app_bar_add_food.*
import kotlinx.android.synthetic.main.content_add_food.*

class AddFoodActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    AdapterView.OnItemSelectedListener {

    var categoryList = arrayOf("Baking & Grains","Beans & Legumes","Beverages",
        "Broths & Soups","Condiments & Sauces","Dairy","Dairy Alternatives",
        "Deserts & Snacks","Fruit","Meat & Poultry","Nuts & Seeds","Oils","Seafood & Fish",
        "Spices, Herbs, Seasonings","Sweeteners","Vegetables","Wheat")
    var categorySelected : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_food)
        setSupportActionBar(toolbar)

        val context = this
        val db = DataBaseHandler(context)

        foodCategory!!.onItemSelectedListener = this
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryList)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        foodCategory!!.adapter = aa

        fab.setOnClickListener {
            if (foodName.text.toString().isNotEmpty() &&
                categorySelected!!.isNotEmpty()
            ) {
                val food = Food(foodName.text.toString(), categorySelected!!)
                val newID = db.insertFood(food)
                val message = newID.toString()
                val intent = Intent(this@AddFoodActivity, FoodItemActivity::class.java)
                intent.putExtra("food", message)
                startActivity(intent)
            } else {
                Toast.makeText(context, "Please Fill Out All details", Toast.LENGTH_SHORT).show()
            }

        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

    }

    ///////////////NAV DRAWER METHODS //////////////////////

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
                val menuIntent = Intent(this@AddFoodActivity, MainActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_cookbook -> {
                val menuIntent = Intent(this@AddFoodActivity, CookbookActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_shopping -> {
                val menuIntent = Intent(this@AddFoodActivity, ShoppingListActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_favourite -> {
                val menuIntent = Intent(this@AddFoodActivity, FavouritesActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_barcode -> {
                val menuIntent = Intent(this@AddFoodActivity, ScanBarcodeActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_share -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    ///////////////// SPINNER METHODS ///////////////////////////////
    override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long) {
        categorySelected = categoryList[position]
    }

    override fun onNothingSelected(arg0: AdapterView<*>) {

    }
}
