package com.example.foodrunner.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.foodrunner.POJO.MenuPOJO
import com.example.foodrunner.R

class MenuAdapter(val context: Context, private val menuList: ArrayList<MenuPOJO>) :
    RecyclerView.Adapter<MenuAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_menu_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = menuList[position]
        holder.primaryText.text = currentItem.menuName
        holder.secondaryText.text = "${currentItem.menuCost}.00"

        holder.btnAdd.setOnClickListener {
            if (holder.btnAdd.text == "Add") {
                holder.btnAdd.setBackgroundColor(ContextCompat.getColor(context, R.color.yellow))
                holder.btnAdd.text = "Remove"
            } else {
                holder.btnAdd.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.purple_700
                    )
                )
                holder.btnAdd.text = "Add"
            }
        }
    }


    override fun getItemCount(): Int {
        return menuList.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val primaryText: TextView = view.findViewById(R.id.tv_primary)
        val secondaryText: TextView = view.findViewById(R.id.tv_secondary)
        val btnAdd: Button = view.findViewById(R.id.btn_add)
    }

}