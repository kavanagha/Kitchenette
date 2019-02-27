package com.kithcenette.kitchenette_v2

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import kotlinx.android.synthetic.main.cupboard_list_item.view.*
import kotlinx.android.synthetic.main.add_quantity_popup.view.*


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

        val layoutInflater = LayoutInflater.from(context)

        holder.tvFoodItem.text = food?.name
        holder.tvQuantityItem.text = food?.quantity.toString()
        holder.tvMeasurement.text = food?.measurement
        holder.btnAddFood.setOnClickListener{
            val window = PopupWindow(context)
            val view = layoutInflater.inflate(R.layout.add_quantity_popup,null)

            window.contentView = view

            val close = view.findViewById<ImageButton>(R.id.btn_close)
            close.setOnClickListener {
                window.dismiss()
            }

            val add = view.findViewById<ImageButton>(R.id.add_qty_btn)
            add.setOnClickListener{
                
            }
            window.showAtLocation(holder.root_layout, Gravity.CENTER,0,0)
        }


    }
}

class ViewHolderCupboard (private val view: View) : RecyclerView.ViewHolder(view) {
    val tvFoodItem = view.tv_food!!
    val tvQuantityItem = view.tvQuantity!!
    val tvMeasurement = view.tvMeasurement!!
    val btnAddFood: ImageButton = view.openQuantityPopup!!
    val root_layout  = view.root_layout

}
