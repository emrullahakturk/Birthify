package com.yargisoft.birthify.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.yargisoft.birthify.R
import com.yargisoft.birthify.models.Birthday

class BirthdayAdapter(private var birthdayList: List<Birthday>,
                      private val onEditClick: (Birthday) -> Unit,
                      private val onDetailClick: (Birthday) -> Unit,
                      val context : Context,
                      private val clickToAddTextView: TextView
    ) : RecyclerView.Adapter<BirthdayAdapter.BirthdayViewHolder>() {

        class BirthdayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val nameTextView: TextView = itemView.findViewById(R.id.nameCardTv)
            val birthdayDateTextView: TextView = itemView.findViewById(R.id.birthdayDateCardTv)
            val noteTextView: TextView = itemView.findViewById(R.id.noteCardTv)
            val editButton: ImageView = itemView.findViewById(R.id.editBirthdayButtonMainPage)
            val toDetailView: CardView = itemView.findViewById(R.id.birthdayCardView)
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
            holder.editButton.setOnClickListener {onEditClick(birthday)}
            holder.toDetailView.setOnClickListener{onDetailClick(birthday)}
        }
        override fun getItemCount(): Int {return birthdayList.size}


        @SuppressLint("NotifyDataSetChanged")
        fun updateData(newBirthdayList: List<Birthday>) {
            clickToAddTextView.visibility = if (newBirthdayList.isEmpty()) View.VISIBLE else View.INVISIBLE
            birthdayList = newBirthdayList
            notifyDataSetChanged()
        }




    }