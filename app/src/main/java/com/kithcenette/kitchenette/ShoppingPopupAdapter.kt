package com.kithcenette.kitchenette

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.list_item_shopping_popup.view.*


class ShoppingPopupAdapter(private val items : ArrayList<String>,val context: Context)
    : RecyclerView.Adapter<ShoppingPopupHolder>() {

    val adapter = this

    override fun onBindViewHolder(p0: ShoppingPopupHolder, p1: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingPopupHolder {
        return ShoppingPopupHolder(
            LayoutInflater.from(context).inflate(R.layout.list_item_shopping_popup,
                parent, false))
    }

    override fun onBindViewHolder(holder: ShoppingPopupHolder, position: Int, payloads: MutableList<Any>) {
        val db = DataBaseHandler(context)

        val food : Food? = db.findFood(items[position].toInt())
        holder.tvFoodItem.text = food?.name
        holder.quantity.text = food?.quantity.toString()
        holder.measurement.text = food?.measurement

        holder.addBtn.setOnClickListener {
            db.addFoodShopping(items[position].toInt())
            items.remove(items[position])
            adapter.notifyDataSetChanged()
        }

        db.close()
    }
}

class ShoppingPopupHolder (view: View) : RecyclerView.ViewHolder(view) {
    val tvFoodItem = view.tv_food!!
    val quantity = view.tv_quantity!!
    val measurement = view.tv_measure!!
    val addBtn = view.add_shop!!
}