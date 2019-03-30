package com.kitchenette.kitchenette

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.AutoCompleteTextView
import com.kitchenette.search.AutoCompleteFoodAdapter
import kotlinx.android.synthetic.main.activity_cupboard.*
import kotlinx.android.synthetic.main.app_bar_cupboard.*
import kotlinx.android.synthetic.main.content_cupboard.*

class CupboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val list : ArrayList<String> = ArrayList()
    private val foodList : ArrayList<Food> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cupboard)
        setSupportActionBar(toolbar)

        /**************************** NAV DRAWER ***********************************/

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        /**************************** RECYCLER VIEW ***********************************/
        addFoodItemsId()
        foodItem.layoutManager = LinearLayoutManager(this)
        foodItem.adapter = CupboardAdapter(list, this)

        /**************************** SEARCH METHODS ***********************************/
        addFoodItems()
        val autocompletetextview = findViewById<AutoCompleteTextView>(R.id.autocompletetextview)
        val adapter = AutoCompleteFoodAdapter(this, foodList)
        autocompletetextview?.threshold=1
        autocompletetextview?.setAdapter(adapter)
        autocompletetextview?.setOnFocusChangeListener {
                _, _ ->
            autocompletetextview.setOnItemClickListener { _, _, _, _ ->
                val db = DataBaseHandler(this)
                val message = db.findFoodName(autocompletetextview.text.toString()).toString()
                val intent = Intent(this@CupboardActivity, FoodItemActivity::class.java)
                intent.putExtra("food", message)
                this.startActivity(intent)
            }
        }

    }

    /**************************** GENERAL METHODS ***********************************/
    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.settings_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**************************** NAVIGATION DRAWER ***********************************/
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_cupboard -> {
                val menuIntent = Intent(this@CupboardActivity, MainActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_cookbook -> {
                val menuIntent = Intent(this@CupboardActivity, CookbookActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_shopping -> {
                val menuIntent = Intent(this@CupboardActivity, ShoppingListActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_favourite -> {
                val menuIntent = Intent(this@CupboardActivity, FavouritesActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_barcode -> {
                val menuIntent = Intent(this@CupboardActivity, ScanBarcodeActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_share -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    /**************************** RECYLER VIEW METHODS ***********************************/
    private fun addFoodItems(){
        val context = this
        val db = DataBaseHandler(context)

        val data = db.readFoodCupboard()

        for(i in 0..(data.size-1))
            foodList.add(data[i])
    }
    private fun addFoodItemsId(){
        val context = this
        val db = DataBaseHandler(context)

        val data = db.readFoodCupboard()

        for(i in 0..(data.size-1))
            list.add(data[i].id.toString())
    }
}
