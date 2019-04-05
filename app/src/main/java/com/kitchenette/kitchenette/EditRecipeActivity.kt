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
import kotlinx.android.synthetic.main.activity_edit_recipe.*
import kotlinx.android.synthetic.main.app_bar_edit_recipe.*
import kotlinx.android.synthetic.main.content_edit_recipe.*

class EditRecipeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    AdapterView.OnItemSelectedListener{

    val list : ArrayList<Food> = ArrayList()
    private val ingredients : ArrayList<String> = ArrayList()
    private val quantityList : ArrayList<Double> = ArrayList()
    private val measureList : ArrayList<String> = ArrayList()
    var bitmap: Bitmap? = null
    private val mealTypeList = arrayOf("Breakfast", "Lunch", "Dinner", "Desserts & Snacks", "Other")
    var selected : String? = null
    private val measurements = arrayOf("cup","dessertspoon","fl. oz",
        "grams","kg","litres","ml","oz","pint","tbsp","tsp", "whole")
    var s : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_recipe)
        setSupportActionBar(toolbar)

        /******************************* SET RECIPE ITEM *************************************/
        val context = this
        val db = DataBaseHandler(context)
        val id: String = intent.getStringExtra("recipe")
        val recipe = db.findRecipe(id.toInt())

        name.setText(recipe?.name)
        description.setText(recipe?.description)
        method.setText(recipe?.method)
        servings.setText(recipe?.servings.toString())
        cuisine.setText(recipe?.cuisine)
        val bitmap: Bitmap? = recipe?.photo
        image.setImageBitmap(bitmap)
        selected = recipe?.mealType

        /********************* FLOATING ACTION BUTTON *******************************/
        fab.setOnClickListener {
            if(name.text.toString().isNotEmpty() && description.text.toString().isNotEmpty() &&
                method.text.toString().isNotEmpty()&& servings.text.toString().isNotEmpty() &&
                cuisine.text.toString().isNotEmpty() && bitmap!= null){

                db.updateRecipe(name.text.toString(), method.text.toString(), cuisine.text.toString(),
                    description.text.toString(), selected!!, bitmap, servings.text.toString().toInt(), id.toInt())

                val intent = Intent(this@EditRecipeActivity, RecipeItemActivity::class.java)
                intent.putExtra("recipe", id)
                startActivity(intent)
            }else
                Toast.makeText(context, "Please Fill Out All details", Toast.LENGTH_SHORT).show()
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED){
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    requestPermissions(permissions, AddFoodActivity.PERMISSION_CODE)
                } else
                    pickImageFromGallery()
            } else
                pickImageFromGallery()
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
                val menuIntent = Intent(this@EditRecipeActivity, MainActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_cookbook -> {
                val menuIntent = Intent(this@EditRecipeActivity, CookbookActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_shopping -> {
                val menuIntent = Intent(this@EditRecipeActivity, ShoppingListActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_favourite -> {
                val menuIntent = Intent(this@EditRecipeActivity, FavouritesActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_barcode -> {
                val menuIntent = Intent(this@EditRecipeActivity, ScanBarcodeActivity::class.java)
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
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    companion object {
        private const val IMAGE_PICK_CODE = 1000
        private const val PERMISSION_CODE = 1001
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    pickImageFromGallery()
                }
                else
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

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

    override fun onNothingSelected(arg0: AdapterView<*>) {}

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

