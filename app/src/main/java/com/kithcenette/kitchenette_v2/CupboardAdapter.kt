package com.kithcenette.kitchenette_v2

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import kotlinx.android.synthetic.main.cupboard_list_item.view.*


class CupboardAdapter(private val items : ArrayList<String>, val context: Context)
    : RecyclerView.Adapter<ViewHolderCupboard>() {

    val adapter = this

    override fun onBindViewHolder(p0: ViewHolderCupboard, p1: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount(): Int {
        return items.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderCupboard {
        return ViewHolderCupboard(LayoutInflater.from(context).inflate(R.layout.cupboard_list_item,
            parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolderCupboard, position: Int, payloads: MutableList<Any>) {
        val db = DataBaseHandler(context)
        val food : Food? = db.findFood(items[position].toInt())

        holder.tvFoodItem.text = food?.name
        holder.tvQuantityItem.text = food?.quantity.toString()
        holder.tvMeasurement.text = food?.measurement
    }
}

class ViewHolderCupboard (private val view: View) : RecyclerView.ViewHolder(view) {
    val tvFoodItem = view.tv_food!!
    val tvQuantityItem = view.tvQuantity!!
    val tvMeasurement = view.tvMeasurement!!

}
