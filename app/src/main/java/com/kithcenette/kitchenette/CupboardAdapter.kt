package com.kithcenette.kitchenette_v2

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.cupboard_list_item.view.*

class CupboardAdapter(private val items : ArrayList<String>, val context: Context)
    : RecyclerView.Adapter<ViewHolderCupboard>(), AdapterView.OnItemSelectedListener {

    private var list = arrayOf("grams","litres")
    private var s : String? = null

    override fun onBindViewHolder(p0: ViewHolderCupboard, p1: Int) {    }

    override fun getItemCount(): Int {
        return items.size
    }

    ///////////////// SPINNER METHODS ///////////////////////////////
    override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long) {
        s = list[position]
    }

    override fun onNothingSelected(arg0: AdapterView<*>) {    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderCupboard {
        return ViewHolderCupboard(LayoutInflater.from(context).inflate(R.layout.cupboard_list_item,
            parent, false))
    }

    @SuppressLint("InflateParams")
    override fun onBindViewHolder(holder: ViewHolderCupboard, position: Int, payloads: MutableList<Any>) {
        val db = DataBaseHandler(context)
        val food : Food? = db.findFoodQuantity(items[position].toInt())
        val id = items[position].toInt()
        val layoutInflater = LayoutInflater.from(context)

        holder.tvFoodItem.text = food?.name
        holder.tvQuantityItem.text = food?.quantity.toString()
        holder.tvMeasurement.text = food?.measurement

        holder.btnAddFood.setOnClickListener{
            val window = PopupWindow(context)
            val view = layoutInflater.inflate(R.layout.add_quantity_popup,null)

            window.isFocusable = true
            window.isOutsideTouchable = true
            window.update()

            window.contentView = view

            val close = view.findViewById<ImageButton>(R.id.btn_close)
            close.setOnClickListener {
                window.dismiss()
            }

            val spinner = view.findViewById<Spinner>(R.id.enter_measurement)

            spinner!!.onItemSelectedListener = this
            val aa = ArrayAdapter(context, android.R.layout.simple_spinner_item, list)
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = aa

            val qty = view.findViewById<EditText>(R.id.enter_quantity)

            val add = view.findViewById<ImageButton>(R.id.add_qty_btn)
            add.setOnClickListener{
                if(qty.text.toString().isNotEmpty() &&
                        s!!.isNotEmpty()){
                    db.addFoodQuantity(id, qty.text.toString().toDouble(),s.toString())
                    notifyDataSetChanged()
                    window.dismiss()
                }
            }
            window.showAtLocation(holder.layout, Gravity.CENTER,0,0)
        }


    }
}

class ViewHolderCupboard (view: View) : RecyclerView.ViewHolder(view) {
    val tvFoodItem = view.tv_food!!
    val tvQuantityItem = view.tvQuantity!!
    val tvMeasurement = view.tvMeasurement!!
    val btnAddFood: ImageButton = view.openQuantityPopup!!
    val layout  = view.root_layout!!

}
