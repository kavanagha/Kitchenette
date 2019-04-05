package com.kitchenette.kitchenette

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Spinner
import com.kitchenette.search.AutoCompleteFoodAdapter
import kotlinx.android.synthetic.main.activity_cookbook.*
import kotlinx.android.synthetic.main.app_bar_cookbook.*


class CookbookActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private var mSectionsPagerAdapter: CookbookActivity.SectionsPagerAdapter? = null
    private val list : ArrayList<Recipe> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cookbook)
        setSupportActionBar(toolbar)

        /************************ TAB  ACTIVITY ************************/
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        container.adapter = mSectionsPagerAdapter

        container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))

        /******************FLOATING ACTION BUTTON *************************/
        fab.setOnClickListener { view ->
            val menuIntent = Intent(this@CookbookActivity, AddRecipeActivity::class.java)
            startActivity(menuIntent)
        }

        /************************ NAVIGATION DRAWER *************************/
        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        /**************************** SEARCH METHODS ***********************************/
        addRecipeItems()
        val autocompletetextview = findViewById<AutoCompleteTextView>(R.id.autocompletetextview)
        val adapter = AutoCompleteRecipeAdapter(this, list)
        autocompletetextview?.threshold=1
        autocompletetextview?.setAdapter(adapter)
        autocompletetextview?.setOnFocusChangeListener {
                _, _ ->
            autocompletetextview.setOnItemClickListener { _, _, _, _ ->
                val db = DataBaseHandler(this)
                val message = db.findRecipeName(autocompletetextview.text.toString()).toString()
                val intent = Intent(this@CookbookActivity, RecipeItemActivity::class.java)
                intent.putExtra("recipe", message)
                this.startActivity(intent)
            }
        }

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
        when (item.itemId) {
            R.id.nav_cupboard -> {
                val menuIntent = Intent(this@CookbookActivity, MainActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_cookbook -> {
                val menuIntent = Intent(this@CookbookActivity, CookbookActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_shopping -> {
                val menuIntent = Intent(this@CookbookActivity, ShoppingListActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_favourite -> {
                val menuIntent = Intent(this@CookbookActivity, FavouritesActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_barcode -> {
                val menuIntent = Intent(this@CookbookActivity, ScanBarcodeActivity::class.java)
                startActivity(menuIntent)
            }
            R.id.nav_share -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    /************************ SEARCH METHODS ***************************/
    private fun addRecipeItems(){
        val context = this
        val db = DataBaseHandler(context)

        val data = db.readRecipeData()

        for(i in 0..(data.size-1))
            list.add(data[i])
    }

    /**************** TAB ACTIVITY METHODS *****************/
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return PlaceholderFragment.newInstance(position + 1)
        }

        override fun getCount(): Int { return 2 }
    }

    class PlaceholderFragment : Fragment() {

        private val list : ArrayList<String> = ArrayList()
        private var mealList : Array<String> = arrayOf("All","Breakfast", "Lunch",
            "Dinner","Desserts & Snacks", "Other")
        private var cuisineList : ArrayList<String> = ArrayList()
        private var dietList : ArrayList<String> = ArrayList()
        private var selectMeal : String? = null
        private var selectCuisine : String? = null
        private var selectDiet : String? = null

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {

            return if(arguments?.getInt(ARG_SECTION_NUMBER)==1) {
                inflater.inflate(R.layout.fragment_cookbook_suggested, container, false)
            } else{
                inflater.inflate(R.layout.fragment_cookbook_all, container, false)
            }
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            val recipeItem = view.findViewById(R.id.recipeItem) as RecyclerView
            fillCuisineList()
            fillDietList()

            if(arguments?.getInt(ARG_SECTION_NUMBER)==1) {
                val spinnerMeal = view.findViewById(R.id.spinner_meal_type) as Spinner
                val spinnerCuisine = view.findViewById(R.id.spinner_cuisine) as Spinner
                val spinnerDiet = view.findViewById(R.id.spinner_diet) as Spinner
                addSuggestItems()


                /** MEAL TYPE FILTER **/
                spinnerMeal.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        adapterView: AdapterView<*>, arg1: View, position: Int, id: Long
                    ) {
                        addSuggestItems()
                        selectMeal = mealList[position]
                        if (selectMeal != null) {
                            if(selectMeal!="All")
                                removeAllList()
                            recipeItem.layoutManager = LinearLayoutManager(activity)
                            recipeItem.adapter = RecipeAdapter(list, activity!!.applicationContext)
                        }
                    }

                    override fun onNothingSelected(arg0: AdapterView<*>) { }
                }

                val am = ArrayAdapter(activity!!.applicationContext,
                    android.R.layout.simple_spinner_item, mealList)
                am.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerMeal.adapter = am

                /** CUISINE FILTER **/

                spinnerCuisine.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        adapterView: AdapterView<*>, arg1: View, position: Int, id: Long
                    ) {
                        addSuggestItems()
                        selectCuisine = cuisineList[position]
                        if (selectCuisine != null) {
                            if(selectCuisine!="All")
                                removeAllList()
                            recipeItem.layoutManager = LinearLayoutManager(activity)
                            recipeItem.adapter = RecipeAdapter(list, activity!!.applicationContext)
                        }
                    }

                    override fun onNothingSelected(arg0: AdapterView<*>) { }
                }

                val adapter = ArrayAdapter<String>(activity!!.applicationContext,
                    android.R.layout.simple_spinner_item, cuisineList)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerCuisine.adapter = adapter

                /** DIET FILTER **/

                spinnerDiet.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        adapterView: AdapterView<*>, arg1: View, position: Int, id: Long
                    ) {
                        addSuggestItems()
                        selectDiet = dietList[position]
                        if (selectDiet != null) {
                            if(selectDiet!="All")
                                removeAllList()
                            recipeItem.layoutManager = LinearLayoutManager(activity)
                            recipeItem.adapter = RecipeAdapter(list, activity!!.applicationContext)
                        }
                    }

                    override fun onNothingSelected(arg0: AdapterView<*>) { }
                }

                val adapterDiet = ArrayAdapter<String>(activity!!.applicationContext,
                    android.R.layout.simple_spinner_item, dietList)
                adapterDiet.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerDiet.adapter = adapterDiet

            } else{
                val spinnerMeal = view.findViewById(R.id.spinner_meal_type) as Spinner
                val spinnerCuisine = view.findViewById(R.id.spinner_cuisine) as Spinner
                val spinnerDiet = view.findViewById(R.id.spinner_diet) as Spinner
                addAllItems()

                /** MEAL TYPE FILTER **/
                spinnerMeal.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        adapterView: AdapterView<*>, arg1: View, position: Int, id: Long
                    ) {
                        addAllItems()
                        selectMeal = mealList[position]
                        if (selectMeal != null) {
                            if(selectMeal!="All")
                                removeAllList()
                            recipeItem.layoutManager = LinearLayoutManager(activity)
                            recipeItem.adapter = RecipeAdapter(list, activity!!.applicationContext)
                        }
                    }

                    override fun onNothingSelected(arg0: AdapterView<*>) { }
                }

                val am = ArrayAdapter(activity!!.applicationContext,
                    android.R.layout.simple_spinner_item, mealList)
                am.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerMeal.adapter = am

                /** CUISINE FILTER **/

                spinnerCuisine.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        adapterView: AdapterView<*>, arg1: View, position: Int, id: Long
                    ) {
                        addAllItems()
                        selectCuisine = cuisineList[position]
                        if (selectCuisine != null) {
                            if(selectCuisine!="All")
                                removeAllList()
                            recipeItem.layoutManager = LinearLayoutManager(activity)
                            recipeItem.adapter = RecipeAdapter(list, activity!!.applicationContext)
                        }
                    }

                    override fun onNothingSelected(arg0: AdapterView<*>) { }
                }

                val adapter = ArrayAdapter<String>(activity!!.applicationContext,
                    android.R.layout.simple_spinner_item, cuisineList)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerCuisine.adapter = adapter

                /** DIET FILTER **/

                spinnerDiet.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        adapterView: AdapterView<*>, arg1: View, position: Int, id: Long
                    ) {
                        addAllItems()
                        selectDiet = dietList[position]
                        if (selectDiet != null) {
                            if(selectDiet!="All")
                                removeAllList()
                           recipeItem.layoutManager = LinearLayoutManager(activity)
                           recipeItem.adapter = RecipeAdapter(list, activity!!.applicationContext)
                        }
                    }

                    override fun onNothingSelected(arg0: AdapterView<*>) { }
                }

                val adapterDiet = ArrayAdapter<String>(activity!!.applicationContext,
                    android.R.layout.simple_spinner_item, dietList)
                adapterDiet.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerDiet.adapter = adapterDiet

            }

            /** ADD ON CLICK LISTENER TO RECYCLER VIEW ITEM **/
            var message:String?
            recipeItem.addOnItemTouchListener(
                RecyclerItemClickListener(
                    activity!!.applicationContext,
                    object : RecyclerItemClickListener.OnItemClickListener {
                        override fun onItemClick(view: View, position: Int) {
                            message = list[position]
                            val intent = Intent(activity!!.applicationContext, RecipeItemActivity::class.java)
                            intent.putExtra("recipe", message)
                            startActivity(intent)
                        }
                    })
            )
        }

        private fun addSuggestItems() {
            val context = activity!!.applicationContext
            val db = DataBaseHandler(context)
            val data: MutableList<Recipe>
            list.clear()
            data = db.suggestRecipes()

            for (i in 0..(data.size - 1))
                list.add(data[i].id.toString())
        }
        private fun addAllItems(){
            val context = activity!!.applicationContext
            val db = DataBaseHandler(context)
            val data : MutableList<Recipe>
            list.clear()
            data = db.readRecipeData()

            for(i in 0..(data.size-1))
                list.add(data[i].id.toString())
        }
        private fun removeAllList(){
            val context = activity!!.applicationContext
            val db = DataBaseHandler(context)

            val temp : ArrayList<String> = ArrayList()
            for(i in 0..(list.size-1))
               temp.add(list[i])

            list.clear()

            /** MEAL TYPE FILTER **/

            for(i in 0..(temp.size-1)){
                val recipe = db.findRecipe(temp[i].toInt())
                if (selectMeal!= null && selectMeal!= "All"){
                    if ( recipe?.mealType == selectMeal)
                        list.add(temp[i])
                } else
                    list.add(temp[i])
            }

            /** CUISINE FILTER **/
            temp.clear()

            for(i in 0..(list.size-1))
                temp.add(list[i])

            list.clear()

            for(i in 0..(temp.size-1)){
                val recipe = db.findRecipe(temp[i].toInt())
                if (selectCuisine!= null && selectCuisine!= "All"){
                    if ( recipe?.cuisine == selectCuisine)
                        list.add(temp[i])
                } else
                    list.add(temp[i])
            }

            /** DIET FILTER **/
            temp.clear()

            for(i in 0..(list.size-1))
                temp.add(list[i])

            list.clear()

            for(i in 0..(temp.size-1)){
                val diet = db.findDietName(temp[i].toInt())
                if (selectDiet!= null && selectDiet!= "All"){
                    if ( diet == selectDiet)
                        list.add(temp[i])
                } else
                    list.add(temp[i])
            }


        }
        private fun fillCuisineList() {
            val context = activity!!.applicationContext
            val db = DataBaseHandler(context)
            addAllItems()
            cuisineList.add("All")

            for (i in 0..(list.size - 1)) {
                val recipe = db.findRecipe(list[i].toInt())
                var found = false
                for (i in 0..(cuisineList.size - 1)) {
                    if (cuisineList[i] == recipe?.cuisine) {
                        found = true
                        break
                    }
                }
                if (!found)
                    cuisineList.add(recipe!!.cuisine)
            }
        }
        private fun fillDietList(){
            val context = activity!!.applicationContext
            val db = DataBaseHandler(context)
            addAllItems()
            dietList.add("All")

            for (i in 0..(list.size - 1)) {
                var found = false
                val name = db.findDietName(list[i].toInt())
                for (i in 0..(dietList.size - 1)) {
                    if (dietList[i]==name) {
                        found = true
                        break
                    }
                }
                if (!found && name!=null)
                    dietList.add(name)
            }
        }

        companion object {

            private const val ARG_SECTION_NUMBER = "section_number"

            fun newInstance(sectionNumber: Int): PlaceholderFragment {
                val fragment = PlaceholderFragment()
                val args = Bundle()
                args.putInt(ARG_SECTION_NUMBER, sectionNumber)
                fragment.arguments = args
                return fragment
            }

        }
    }
}
