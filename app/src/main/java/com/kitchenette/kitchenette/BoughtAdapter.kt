package com.kitchenette.kitchenette

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.list_item_bought.view.*

class BoughtAdapter(private val items : ArrayList<String>, val context: Context)
    : RecyclerView.Adapter<ViewHolderBought>(), AdapterView.OnItemSelectedListener {

    private val adapter = this
    private var list = arrayOf("cup","dessertspoon","fl. oz",
        "grams","kg","litres","ml","oz","pint","tbsp","tsp", "whole")
    private var s : String? = null

    override fun onBindViewHolder(p0: ViewHolderBought, p1: Int) {    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderBought {
        return ViewHolderBought(LayoutInflater.from(context).inflate(R.layout.list_item_bought,
            parent, false))
    }

    /****************************** SPINNER METHODS ********************************/
    override fun onItemSelected(arg0: AdapterView<*>, arg1: View, position: Int, id: Long) {
        s = list[position]
    }

    override fun onNothingSelected(arg0: AdapterView<*>) {    }

    private fun onClickMethod(position:Int){
        val message = items[position]
        val intent = Intent(context, FoodItemActivity::class.java)
        intent.putExtra("food", message)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    @SuppressLint("InflateParams")
    override fun onBindViewHolder(holder: ViewHolderBought, position: Int, payloads: MutableList<Any>) {
        val db = DataBaseHandler(context)
        val food = db.findFood(items[position].toInt())
        val id = items[position].toInt()
        val layoutInflater = LayoutInflater.from(context)

        if(food?.photo!= null){
            val bitmap: Bitmap? = food.photo
            holder.image.setImageBitmap(bitmap)
        }
        holder.image.setOnClickListener {
            onClickMethod(position)}
        holder.tvFoodItem.text = food?.name
        holder.tvFoodItem.setOnClickListener {
            onClickMethod(position)}
        holder.buttonAddShop.setOnClickListener{
            db.addFoodShopping(items[position].toInt())
            db.removeFoodBought(items[position].toInt())
            items.remove(items[position])
            adapter.notifyDataSetChanged()
        }

        holder.buttonRemoveList.setOnClickListener{
            db.removeFoodBought(items[position].toInt())
            items.remove(items[position])
            adapter.notifyDataSetChanged()
        }

        holder.buttonAddCupboard.setOnClickListener{
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
                if(qty.text.toString().isNotEmpty() && s!!.isNotEmpty()){
                    db.addFoodQuantity(id, qty.text.toString().toDouble(),s.toString())
                    db.addFoodCupboard(id)
                    db.removeFoodBought(id)
                    items.remove(items[position])
                    notifyDataSetChanged()
                    window.dismiss()
                }
            }
            window.showAtLocation(holder.layout, Gravity.CENTER,0,0)
        }
    }
}

class ViewHolderBought (view: View) : RecyclerView.ViewHolder(view) {
    val tvFoodItem = view.tv_foodBought!!
    val buttonAddShop: ImageButton = view.addShop
    val buttonAddCupboard:ImageButton = view.addCupboard
    val buttonRemoveList: ImageButton = view.removeList
    val layout  = view.bought_layout!!
    val image = view.image_food_icon!!
}
