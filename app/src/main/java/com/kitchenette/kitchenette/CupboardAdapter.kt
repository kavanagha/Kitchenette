package com.kitchenette.kitchenette

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.list_item_cupboard.view.*

class CupboardAdapter(private val items : ArrayList<String>, val context: Context)
    : RecyclerView.Adapter<ViewHolderCupboard>(), AdapterView.OnItemSelectedListener {

    private var list = arrayOf("cup","dessertspoon","fl. oz",
        "grams","kg","litres","ml","oz","pint","tbsp","tsp", "whole")
    private var s : String? = null

    override fun onBindViewHolder(p0: ViewHolderCupboard, p1: Int) {    }

    override fun getItemCount(): Int {
        return items.size
    }

    /***************************** SPINNER METHODS **************************************/
    override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long) {
        s = list[position]
    }

    override fun onNothingSelected(arg0: AdapterView<*>) {    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderCupboard {
        return ViewHolderCupboard(LayoutInflater.from(context).inflate(
            R.layout.list_item_cupboard,
            parent, false))
    }
    private fun onClickMethod(position:Int){
        val message = items[position]
        val intent = Intent(context, FoodItemActivity::class.java)
        intent.putExtra("food", message)
        context.startActivity(intent)
    }

    @SuppressLint("InflateParams")
    override fun onBindViewHolder(holder: ViewHolderCupboard, position: Int, payloads: MutableList<Any>) {
        val db = DataBaseHandler(context)
        val food : Food? = db.findFood(items[position].toInt())
        val id = items[position].toInt()
        val layoutInflater = LayoutInflater.from(context)

        holder.tvFoodItem.text = food?.name
        holder.tvFoodItem.setOnClickListener{
            onClickMethod(position)}
        holder.tvQuantityItem.text = food?.quantity.toString()
        holder.tvQuantityItem.setOnClickListener {
            onClickMethod(position)}
        holder.tvMeasurement.text = food?.measurement
        holder.tvMeasurement.setOnClickListener {
            onClickMethod(position)}
        holder.image.setOnClickListener {
            onClickMethod(position)}
        if(food?.photo!= null){
            val bitmap: Bitmap? = food.photo
            holder.image.setImageBitmap(bitmap)
        }

        holder.btnAddFood.setOnClickListener{
            val window = PopupWindow(context)
            val view = layoutInflater.inflate(R.layout.popup_add_quantity,null)

            window.isFocusable = true
            window.isOutsideTouchable = true
            window.update()

            window.contentView = view

            val oqty = view.findViewById<TextView>(R.id.old_qty)
            oqty.text  = food?.quantity.toString()
            val omsr = view.findViewById<TextView>(R.id.old_msr)
            omsr.text  = food?.measurement

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

        holder.btnDelFood.setOnClickListener {
            val window = PopupWindow(context)
            val view = layoutInflater.inflate(R.layout.popup_remove_quantity,null)

            window.isFocusable = true
            window.isOutsideTouchable = true
            window.update()

            window.contentView = view

            val oqty = view.findViewById<TextView>(R.id.old_qty)
            oqty.text  = food?.quantity.toString()
            val omsr = view.findViewById<TextView>(R.id.old_msr)
            omsr.text  = food?.measurement

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
                    db.delFoodQuantity(id, qty.text.toString().toDouble(),s.toString())
                    val f : Food? = db.findFood(id)
                    if(f?.quantity == 0.0) {
                        items.remove(items[position])
                        db.addFoodShopping(id)
                    }
                    notifyDataSetChanged()
                    window.dismiss()
                }
            }
            window.showAtLocation(holder.layout, Gravity.CENTER,0,0)
        }
        db.close()
    }
}

class ViewHolderCupboard (view: View) : RecyclerView.ViewHolder(view) {
    val tvFoodItem = view.tv_food!!
    val tvQuantityItem = view.tvQuantity!!
    val tvMeasurement = view.tvMeasurement!!
    val btnAddFood: ImageButton = view.openQuantityPopup!!
    val btnDelFood: ImageButton = view.openDeleteQuantityPopup!!
    val layout  = view.root_layout!!
    val image: ImageView = view.image_food_icon!!
}
