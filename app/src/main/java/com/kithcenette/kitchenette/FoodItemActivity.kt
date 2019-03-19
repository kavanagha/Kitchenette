package com.kithcenette.kitchenette

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_food_item.*
import kotlinx.android.synthetic.main.app_bar_food_item.*
import kotlinx.android.synthetic.main.content_food_item.*
import kotlinx.android.synthetic.main.nav_header.*

import android.graphics.Color
import android.widget.ImageButton



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

        var food : Food? = db.findFood(foodMessage.toInt())

        foodName.text = food?.name
        foodItem_category.text = food?.category
        val bitmap: Bitmap? = food?.photo
        image.setImageBitmap(bitmap)

        if(food?.shoppingList==1)
            shoppingButton.setColorFilter(Color.argb(255, 0, 191, 255))
        else
            shoppingButton.setColorFilter(Color.argb(0, 0, 0, 0))

        if(food?.favourite==1)
            favButton.setColorFilter(Color.argb(255, 0, 191, 255))
        else
            favButton.setColorFilter(Color.argb(0, 0, 0, 0))

        //////////////////////////////////// BUTTONS /////////////////////////////////////

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Update Quantity", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

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
}
