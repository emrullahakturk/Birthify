package com.yargisoft.birthify.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.yargisoft.birthify.R
import com.yargisoft.birthify.models.Birthday

class DeletedBirthdayAdapter(private var deletedBirthdayList: List<Birthday>,
                             private val onDetailClick: (Birthday) -> Unit,
                             val context : Context,
                            private val textView: TextView
) : RecyclerView.Adapter<DeletedBirthdayAdapter.DeletedBirthdayViewHolder>() {

    val preferences: SharedPreferences = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE)


    class DeletedBirthdayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nameCardTv)
        val birthdayDateTextView: TextView = itemView.findViewById(R.id.birthdayDateCardTv)
        val toDetailView: CardView = itemView.findViewById(R.id.birthdayCardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeletedBirthdayViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_deleted_birthday, parent, false)

        return DeletedBirthdayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeletedBirthdayViewHolder, position: Int) {
        val birthday = deletedBirthdayList[position]
        holder.nameTextView.text = birthday.name
        holder.birthdayDateTextView.text = when (preferences.getString("AppLanguage", null)) {
            "en" -> "Birthday Date: ${birthday.birthdayDate}"
            "tr" -> "Doğum Tarihi: ${birthday.birthdayDate}"
            else -> "Birthday Date: ${birthday.birthdayDate}"
        }
        holder.toDetailView.setOnClickListener{
            onDetailClick(birthday)
        }

    }

    override fun getItemCount(): Int {
        return deletedBirthdayList.size
    }


    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newBirthdays: List<Birthday>) {
        textView.visibility = if (newBirthdays.isEmpty()) View.VISIBLE else View.INVISIBLE
        deletedBirthdayList = newBirthdays
        notifyDataSetChanged()
    }



}
