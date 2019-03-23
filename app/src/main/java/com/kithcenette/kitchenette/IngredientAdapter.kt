package com.kithcenette.kitchenette

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.list_item_ingredient.view.*


class IngredientAdapter(private val items : ArrayList<String>,
                        private val ingredientID : ArrayList<String>, val context: Context)
    : RecyclerView.Adapter<IngredientViewHolder>() {

    override fun onBindViewHolder(p0: IngredientViewHolder, p1: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        return IngredientViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_ingredient,
            parent, false))
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int, payloads: MutableList<Any>) {
        val db = DataBaseHandler(context)

        val food : Food? = db.findFood(items[position].toInt())
        holder.tvFoodItem.text = food?.name

        val ingredient : Ingredients? = db.findIngredient(ingredientID[position].toInt())
        holder.quantity.text = ingredient?.quantity.toString()
        holder.measurement.text = ingredient?.measurement
        db.close()
    }
}

class IngredientViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    val tvFoodItem = view.tv_food!!
    val quantity = view.tv_quantity!!
    val measurement = view.tv_measure!!
}