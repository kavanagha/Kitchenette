package com.kithcenette.kitchenette

import android.content.Context
import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.list_item_recipe.view.*


class RecipeAdapter(private val items : ArrayList<String>, val context: Context) : RecyclerView.Adapter<RecipeView>() {


    override fun onBindViewHolder(p0: RecipeView, p1: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeView {
        return RecipeView(LayoutInflater.from(context).inflate(R.layout.list_item_recipe, parent, false))
    }

    override fun onBindViewHolder(holder: RecipeView, position: Int, payloads: MutableList<Any>) {
        val db = DataBaseHandler(context)

        val recipe : Recipe? = db.findRecipe(items[position].toInt())

        holder.tvRecipeItem.text = recipe?.name

        if(recipe?.photo!= null){
            val bitmap: Bitmap? = recipe?.photo
            holder.image.setImageBitmap(bitmap)
        }
    }
}

class RecipeView (view: View) : RecyclerView.ViewHolder(view) {
    val tvRecipeItem = view.tv_recipe!!
    val image = view.image_recipe_icon!!
}
