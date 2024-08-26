package com.yargisoft.birthify.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
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

    val preferences: SharedPreferences = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE)


        class BirthdayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val nameTextView: TextView = itemView.findViewById(R.id.nameCardTv)
            val birthdayDateTextView: TextView = itemView.findViewById(R.id.birthdayDateCardTv)
            val editButton: ImageView = itemView.findViewById(R.id.editBirthdayButtonMainPage)
            val toDetailView: CardView = itemView.findViewById(R.id.birthdayCardView)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BirthdayViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_birthday, parent, false)
            return BirthdayViewHolder(view)
        }

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: BirthdayViewHolder, position: Int) {
            val birthday = birthdayList[position]
            holder.nameTextView.text = birthday.name
            holder.birthdayDateTextView.text = when (preferences.getString("AppLanguage", null)) {
                "en" -> "A new chapter begins on ${birthday.birthdayDate}!"
                "tr" -> "Doğum Tarihi: ${birthday.birthdayDate}"
                else -> "A fresh start on " + birthday.birthdayDate + " awaits!"
            }

            holder.editButton.setOnClickListener {onEditClick(birthday)}
            holder.toDetailView.setOnClickListener{onDetailClick(birthday)}
        }


        override fun getItemCount(): Int {
            return birthdayList.size
        }


        @SuppressLint("NotifyDataSetChanged")
        fun updateData(newBirthdayList: List<Birthday>) {
            clickToAddTextView.visibility = if (newBirthdayList.isEmpty()) View.VISIBLE else View.INVISIBLE
            birthdayList = newBirthdayList
            notifyDataSetChanged()
        }

    }