package com.kitchenette.kitchenette

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.ImageView
import android.widget.TextView
import java.util.ArrayList

class AutoCompleteRecipeAdapter(private val context: Activity, private var items : ArrayList<Recipe>)
    : ArrayAdapter<Recipe>(context, R.layout.list_item_recipe, items) {


    private var resultList: MutableList<Recipe> = ArrayList()

    override fun getCount(): Int {
        return resultList.size
    }
    override fun getItem(index: Int): Recipe {
        return resultList[index]
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        var view = view
        if (view == null) {
            val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inflater.inflate(R.layout.list_item_recipe, parent, false)
        }

        val db = DataBaseHandler(context)

        val recipe : Recipe? = db.findRecipe(resultList[position].id)

        val tvRecipe = view!!.findViewById<View>(R.id.tv_recipe) as TextView
        val image = view.findViewById<View>(R.id.image_recipe_icon) as ImageView
        tvRecipe.text = recipe?.name
        if(recipe?.photo!=null){
            val bitmap: Bitmap? = recipe.photo
            image.setImageBitmap(bitmap)
        }

        return view
    }

    override fun getFilter() = filter

    private var filter: Filter = object : Filter() {

        override fun performFiltering(constraint: CharSequence?): Filter.FilterResults {
            val results = FilterResults()

            val query = if (constraint != null && constraint.isNotEmpty()) autocomplete(constraint.toString())
            else arrayListOf()

            results.values = query
            results.count = query.size

            return results
        }

        private fun autocomplete(input: String): ArrayList<Recipe> {
            val results = arrayListOf<Recipe>()

            for (recipe in items) {
                if (recipe.name.toLowerCase().contains(input.toLowerCase())) results.add(recipe)
            }

            return results
        }

        override fun publishResults(constraint: CharSequence?, results: Filter.FilterResults) {
            resultList = results.values as ArrayList<Recipe>
            notifyDataSetInvalidated()
        }

        override fun convertResultToString(result: Any) = (result as Recipe).name
    }
}