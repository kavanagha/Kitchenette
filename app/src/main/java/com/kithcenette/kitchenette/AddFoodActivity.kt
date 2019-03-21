package com.kithcenette.kitchenette

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.NavigationView
import android.support.v4.content.ContextCompat.startActivity
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
        "Desserts & Snacks","Fruit","Meat & Poultry","Nuts & Seeds","Oils","Seafood & Fish",
        "Spices, Herbs, Seasonings","Sweeteners","Vegetables")
    var categorySelected : String? = null

    var bitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_food)
        setSupportActionBar(toolbar)

        val context = this
        val db = DataBaseHandler(context)

        /**************************** SPINNER **************************************/

        foodCategory!!.onItemSelectedListener = this
        val aa = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryList)
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        foodCategory!!.adapter = aa

        /*************************** UPLOAD IMAGE ******************************/
        upload_image.setOnClickListener {
            //check runtime permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED){
                    //permission denied
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                    //show popup to request runtime permission
                    requestPermissions(permissions, PERMISSION_CODE)
                }
                else{
                    pickImageFromGallery() //permission already granted
                }
            }
            else{
                pickImageFromGallery() //system OS is < Marshmallow
            }
        }

        /********************** FLOATING ACTION BUTTON *************************/

        fab.setOnClickListener {
            if (foodName.text.toString().isNotEmpty() &&
                categorySelected!!.isNotEmpty() &&
                        bitmap != null
            ) {
                val food = Food(foodName.text.toString(), categorySelected!!, bitmap!!)
                val newID = db.insertFood(food)
                val message = newID.toString()
                val intent = Intent(this@AddFoodActivity, FoodItemActivity::class.java)
                intent.putExtra("food", message)
                startActivity(intent)
            } else {
                Toast.makeText(context, "Please Fill Out All details", Toast.LENGTH_SHORT).show()
            }

        }

        /************************ NAVIGATION DRAWER ********************************/

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

    }

    /************************NAV DRAWER METHODS **************************/

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

    /*************************** SPINNER METHODS *********************************/
    override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long) {
        categorySelected = categoryList[position]
    }

    override fun onNothingSelected(arg0: AdapterView<*>) {
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
        internal const val PERMISSION_CODE = 1001
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
}
