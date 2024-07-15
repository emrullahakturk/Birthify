package com.yargisoft.birthify.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yargisoft.birthify.R
import com.yargisoft.birthify.models.Birthday

class BirthdayAdapter(private var birthdayList: List<Birthday>) : RecyclerView.Adapter<BirthdayAdapter.BirthdayViewHolder>() {

    class BirthdayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nameCardTv)
        val birthdayDateTextView: TextView = itemView.findViewById(R.id.birthdayDateCardTv)
        val noteTextView: TextView = itemView.findViewById(R.id.noteCardTv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BirthdayViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.birthday_item, parent, false)
        return BirthdayViewHolder(view)
    }

    override fun onBindViewHolder(holder: BirthdayViewHolder, position: Int) {
        val birthday = birthdayList[position]
        holder.nameTextView.text = birthday.name
        holder.birthdayDateTextView.text = birthday.birthdayDate
         holder.noteTextView.text = birthday.note
    }

    override fun getItemCount(): Int {
        return birthdayList.size
    }

    fun updateData(newList: List<Birthday>) {
        birthdayList = newList
        notifyDataSetChanged()
    }
}