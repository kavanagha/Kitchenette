package com.kithcenette.kitchenette

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.*
import kotlinx.android.synthetic.main.activity_recipe_item.*
import kotlinx.android.synthetic.main.app_bar_recipe_item.*
import kotlinx.android.synthetic.main.content_recipe_item.*

class RecipeItemActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val foodList : ArrayList<String> = ArrayList()
    private val idList : ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_item)
        setSupportActionBar(toolbar)

        val context = this
        val db = DataBaseHandler(context)
        val id: String = intent.getStringExtra("recipe")
        addIngredients(id.toInt())

        /***************FLOATING ACTION BUTTONS ******************/

        // "Make This" Button
        fab.setOnClickListener {
            makeThisPopup(id.toInt())
        }
        /*********************** NAVIGATION ***********************/

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        /**************  FILL PAGE *******************************/
        var recipe : Recipe? = db.findRecipe(id.toInt())

        recipeName.text = recipe?.name
        serving.text = recipe?.servings.toString()
        cuisine.text = recipe?.cuisine
        description.text = recipe?.description
        method.text = recipe?.method

        val bitmap: Bitmap? = recipe?.photo
        image.setImageBitmap(bitmap)

        ingredientItem.layoutManager = LinearLayoutManager(this)
        ingredientItem.adapter = IngredientAdapter(foodList, idList, this)

        /************************* Buttons ****************************/

        if(recipe?.favourite==1)
            favButton.setColorFilter(Color.argb(255, 0, 191, 255))
        else
            favButton.setColorFilter(Color.argb(0, 0, 0, 0))

        shoppingButton.setOnClickListener {
            shoppingPopup()
        }

        favButton.setOnClickListener {
            recipe = if(recipe?.favourite==0) {
                db.addRecipeFavourites(id.toInt())
                favButton.setColorFilter(Color.argb(255, 0, 191, 255))
                db.findRecipe(id.toInt())
            } else {
                db.removeRecipeFavourites(id.toInt())
                favButton.setColorFilter(Color.argb(0, 0, 0, 0))
                db.findRecipe(id.toInt())
            }
        }

        db.close()
    }

    /**************** NAVIGATION METHODS *****************/
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_cupboard -> {
                val menuIntent = Intent(this@RecipeItemActivity, MainActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_cookbook -> {
                val menuIntent = Intent(this@RecipeItemActivity, CookbookActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_shopping -> {
                val menuIntent = Intent(this@RecipeItemActivity, ShoppingListActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_favourite -> {
                val menuIntent = Intent(this@RecipeItemActivity, FavouritesActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_barcode -> {
                val menuIntent = Intent(this@RecipeItemActivity, ScanBarcodeActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_share -> {

            }
        }
        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    /**************** INGREDIENTS METHODS *****************/
    private fun addIngredients(id:Int){
        val context = this
        val db = DataBaseHandler(context)
        val data = db.readIngredients(id)

        for(i in 0..(data.size-1)) {
            foodList.add(data[i].foodID.toString())
            idList.add(data[i].id.toString())
        }
    }

    private fun deleteQuantity(id:Int){
        val context = this
        val db = DataBaseHandler(context)
        val data = db.readIngredients(id)
        for(i in 0..(data.size-1)) {
            db.removeQuantityCupboard(data[i].id, data[i].foodID)
        }
    }

    /**************** POPUP METHODS *****************/

    private fun shoppingPopup(){
        val context = this
        val db = DataBaseHandler(context)
        val window = PopupWindow(context)
        val view = layoutInflater.inflate(R.layout.popup_add_shopping,null)

        window.isFocusable = true
        window.isOutsideTouchable = true
        window.width = LinearLayout.LayoutParams.MATCH_PARENT
        window.update()

        window.contentView = view

        val foodItem = view.findViewById<RecyclerView>(R.id.ingredient_list)
        foodItem.layoutManager = LinearLayoutManager(this)
        foodItem.adapter = ShoppingPopupAdapter(foodList,  this)

        //window.dismiss()
        val add = view.findViewById<ImageButton>(R.id.add_all)
        add.setOnClickListener{
            for(i in 0..(foodList.size-1))
                db.addFoodShopping(foodList[i].toInt())
            window.dismiss()
        }

        val close  = view.findViewById<ImageButton>(R.id.add_none)
        close.setOnClickListener {
            window.dismiss()
        }
        db.close()
        window.showAtLocation(root_layout, Gravity.CENTER,0,0)
    }

    private fun makeThisPopup(id:Int){
        val context = this
        val db = DataBaseHandler(context)
        val window = PopupWindow(context)
        val view = layoutInflater.inflate(R.layout.popup_make_this,null)

        window.isFocusable = true
        window.isOutsideTouchable = true
        window.update()

        window.contentView = view

        val make = view.findViewById<ImageButton>(R.id.make)
        make.setOnClickListener{
            deleteQuantity(id)
            window.dismiss()
            shoppingPopup()
        }

        val close  = view.findViewById<ImageButton>(R.id.no_make)
        close.setOnClickListener {
            window.dismiss()
        }
        db.close()
        window.showAtLocation(root_layout, Gravity.CENTER,0,0)
    }
}
