package com.kithcenette.kitchenette_v2

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_food_item.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_food_item.*

class FoodItemActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_item)
        setSupportActionBar(toolbar)

        ////////////////////////////////// NAV DRAWER ////////////////////////////////////////
        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        ///////////////////// SET FOOD ITEM /////////////////////////////////////////////////

        val context = this
        val db = DataBaseHandler(context)

        val foodMessage: String = intent.getStringExtra("food")

        val food : Food? = db.findFood(foodMessage.toInt())

        foodName.text = food?.name
        foodItem_category.text = food?.category
        val bitmap: Bitmap? = food?.photo
        image.setImageBitmap(bitmap)

        //////////////////////////////////// BUTTONS /////////////////////////////////////

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        shoppingButton.setOnClickListener {
            db.addFoodShopping(foodMessage.toInt())
        }

        favButton.setOnClickListener {
            db.addFoodFavourites(foodMessage.toInt())
        }

        removeFavourites.setOnClickListener {
            db.removeFoodFavourites(foodMessage.toInt())
        }

        addCupboard.setOnClickListener {
            db.addFoodCupboard(foodMessage.toInt())
        }

        removeCupboard.setOnClickListener {
            db.removeFoodCupboard(foodMessage.toInt())
        }


    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.food_item, menu)
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
}
