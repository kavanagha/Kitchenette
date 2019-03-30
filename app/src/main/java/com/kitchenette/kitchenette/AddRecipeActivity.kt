package com.kitchenette.kitchenette

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.kitchenette.search.AutoCompleteFoodAdapter
import kotlinx.android.synthetic.main.activity_add_recipe.*
import kotlinx.android.synthetic.main.app_bar_add_recipe.*
import kotlinx.android.synthetic.main.content_add_recipe.*
import kotlinx.android.synthetic.main.content_add_recipe.root_layout


class AddRecipeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    AdapterView.OnItemSelectedListener {

    val list : ArrayList<Food> = ArrayList()
    val ingredients : ArrayList<String> = ArrayList()
    val quantityList : ArrayList<Double> = ArrayList()
    val measureList : ArrayList<String> = ArrayList()
    var bitmap: Bitmap? = null
    private val mealTypeList = arrayOf("Breakfast", "Lunch", "Dinner", "Desserts & Snacks", "Other")
    var selected : String? = null
    private val measurements = arrayOf("cup","dessertspoon","fl. oz",
        "grams","kg","litres","ml","oz","pint","tbsp","tsp", "whole")
    var s : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_recipe)
        setSupportActionBar(toolbar)

        /********************* FLOATING ACTION BUTTON *******************************/
        fab.setOnClickListener {
            val context = this
            val db = DataBaseHandler(context)

            if(name.text.toString().isNotEmpty() &&
                    description.text.toString().isNotEmpty() &&
                    method.text.toString().isNotEmpty()&&
                    servings.text.toString().isNotEmpty() &&
                    cuisine.text.toString().isNotEmpty() &&
                    bitmap!= null &&
                    ingredients.isNotEmpty() &&
                    quantityList.isNotEmpty() &&
                    measureList.isNotEmpty()){

                val recipe = Recipe(name.text.toString(),selected.toString(), cuisine.text.toString(),
                    servings.text.toString().toInt(),description.text.toString(),method.text.toString(), bitmap!!)
                val newRecipeID = db.insertRecipe(recipe)

                if (newRecipeID!=null){
                    for (i in 0..(ingredients.size-1)) {
                        val ing = Ingredients(newRecipeID, ingredients[i].toInt(), quantityList[i], measureList[i])
                        db.insertIngredient(ing)
                    }
                    if(diet.text.toString().isNotEmpty()){
                        val diet = Diet(diet.text.toString(), newRecipeID)
                        db.insertDiet(diet)
                    }
                }
                val message = newRecipeID.toString()
                val intent = Intent(this@AddRecipeActivity, RecipeItemActivity::class.java)
                intent.putExtra("recipe", message)
                startActivity(intent)
            }else {
                Toast.makeText(context, "Please Fill Out All details", Toast.LENGTH_SHORT).show()
            }

        }

        /*********************** NAVIGATION **********************************/
        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        /*************************** UPLOAD IMAGE ******************************/
        upload_image.setOnClickListener {
            //check runtime permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED){
                    //permission denied
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    //show popup to request runtime permission
                    requestPermissions(permissions, AddFoodActivity.PERMISSION_CODE)
                }
                else{
                    pickImageFromGallery() //permission already granted
                }
            }
            else{
                pickImageFromGallery() //system OS is < Marshmallow
            }
        }


        /**************************** SPINNER **************************************/

        meal_type!!.onItemSelectedListener = this
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, mealTypeList)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        meal_type!!.adapter = aa

        /**************************** SEARCH METHODS ***********************************/

        addFoodItems()

        val adapter = AutoCompleteFoodAdapter(this, list)
        autocompletetextview?.threshold=1
        autocompletetextview?.setAdapter(adapter)
        autocompletetextview?.setOnFocusChangeListener {
                _, _ ->
            autocompletetextview.setOnItemClickListener { _, _, _, _ ->
                val db = DataBaseHandler(this)
                val message = db.findFoodName(autocompletetextview.text.toString()).toString()

                ingredientPopup(message.toInt())
                autocompletetextview.text.clear()
                val item = findViewById<RecyclerView>(R.id.ingredient_list)
                item.layoutManager = LinearLayoutManager(this)
                item.adapter = AddIngredientAdapter(ingredients, quantityList,measureList, this)
            }
        }
    }

    /************************** NAVIGATION METHODS ******************************************/
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
                val menuIntent = Intent(this@AddRecipeActivity, MainActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_cookbook -> {
                val menuIntent = Intent(this@AddRecipeActivity, CookbookActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_shopping -> {
                val menuIntent = Intent(this@AddRecipeActivity, ShoppingListActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_favourite -> {
                val menuIntent = Intent(this@AddRecipeActivity, FavouritesActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_barcode -> {
                val menuIntent = Intent(this@AddRecipeActivity, ScanBarcodeActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_share -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    /************************** UPLOAD IMAGE METHODS ******************************************/
    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    companion object {
        //image pick code
        private const val IMAGE_PICK_CODE = 1000
        //Permission code
        private const val PERMISSION_CODE = 1001
    }

    //handle requested permission result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    //permission from popup granted
                    pickImageFromGallery()
                }
                else{
                    //permission from popup denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    //handle result of picked image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
            image.setImageURI(data?.data)
            image.cropToPadding
            bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, data?.data)
        }
    }

    /*************************** SPINNER METHODS *********************************/
    override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        if(parent.id == R.id.meal_type)
            selected = mealTypeList[position]
        else if(parent.id == R.id.enter_measurement)
            s = measurements[position]

    }


    override fun onNothingSelected(arg0: AdapterView<*>) {
    }

    /**************************** ADD FOOD ITEMS TO LIST METHODS ***********************************/
    private fun addFoodItems() {
        val context = this
        val db = DataBaseHandler(context)

        val data = db.readFoodData()

        for(i in 0..(data.size-1)){
            list.add(data[i])
        }
    }

    /**************************** POPUP METHODS***********************************/
    private fun ingredientPopup(id:Int){
        val context = this
        val db = DataBaseHandler(context)
        val window = PopupWindow(context)
        val view = layoutInflater.inflate(R.layout.popup_add_ingredient,null)

        window.isFocusable = true
        window.isOutsideTouchable = true
        window.update()
        window.width = LinearLayout.LayoutParams.MATCH_PARENT

        window.contentView = view

        view.findViewById<Spinner>(R.id.enter_measurement)!!.onItemSelectedListener = this
        val a = ArrayAdapter(this,
            android.R.layout.simple_spinner_item, this.measurements)
        a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        view.findViewById<Spinner>(R.id.enter_measurement)!!.adapter = a


        val food : Food? = db.findFood(id)
        val foodLabel = view.findViewById<TextView>(R.id.food)
        foodLabel.text = food?.name

        val qty = view.findViewById<EditText>(R.id.enter_quantity)


        val add = view.findViewById<ImageButton>(R.id.add_qty_btn)
        add.setOnClickListener{
            ingredients.add(id.toString())
            measureList.add(s!!)
            val amt  = qty!!.text.toString().toDouble()
            quantityList.add(amt)
            window.dismiss()
        }

        val close  = view.findViewById<ImageButton>(R.id.cancel)
        close.setOnClickListener {
            window.dismiss()
        }
        db.close()
        window.showAtLocation(root_layout, Gravity.CENTER,0,0)
    }
}