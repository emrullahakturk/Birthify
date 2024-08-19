package com.yargisoft.birthify.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.yargisoft.birthify.R
import com.yargisoft.birthify.models.Birthday

class PastBirthdayAdapter(private var pastBirthdayList: List<Birthday>,
                             private val onDetailClick: (Birthday) -> Unit,
                             val context : Context,
                             private val textView: TextView,
) : RecyclerView.Adapter<PastBirthdayAdapter.PastBirthdayViewHolder>() {



    class PastBirthdayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nameCardTv)
        val birthdayDateTextView: TextView = itemView.findViewById(R.id.birthdayDateCardTv)
        val noteTextView: TextView = itemView.findViewById(R.id.noteCardTv)
        val toDetailView: CardView = itemView.findViewById(R.id.birthdayCardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PastBirthdayViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_past_birthday, parent, false)

        return PastBirthdayViewHolder(view)
    }

    override fun onBindViewHolder(holder: PastBirthdayViewHolder, position: Int) {
        val birthday = pastBirthdayList[position]
        holder.nameTextView.text = birthday.name
        holder.birthdayDateTextView.text = birthday.birthdayDate
        holder.noteTextView.text = birthday.note
        holder.toDetailView.setOnClickListener{
            onDetailClick(birthday)
        }

    }

    override fun getItemCount(): Int {
        return pastBirthdayList.size
    }


    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newBirthdays: List<Birthday>) {
        textView.visibility = if (newBirthdays.isEmpty()) View.VISIBLE else View.INVISIBLE
        pastBirthdayList = newBirthdays
        notifyDataSetChanged()
    }



}
