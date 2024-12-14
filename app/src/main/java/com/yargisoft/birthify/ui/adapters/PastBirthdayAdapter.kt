package com.yargisoft.birthify.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.yargisoft.birthify.R
import com.yargisoft.birthify.data.models.Birthday
import com.yargisoft.birthify.utils.sharedpreferences.UserConstants.LANGUAGE_KEY
import com.yargisoft.birthify.utils.sharedpreferences.UserConstants.PREFS_SETTINGS
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PastBirthdayAdapter @Inject constructor(
    @ApplicationContext private val context: Context,
) : RecyclerView.Adapter<PastBirthdayAdapter.PastBirthdayViewHolder>(), AdapterInterface {

    private var pastBirthdayList: List<Birthday> = emptyList()
    private val preferences = context.getSharedPreferences(PREFS_SETTINGS, Context.MODE_PRIVATE)

    class PastBirthdayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nameCardTv)
        val birthdayDateTextView: TextView = itemView.findViewById(R.id.birthdayDateCardTv)
     }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PastBirthdayViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_past_birthday, parent, false)
        return PastBirthdayViewHolder(view)
    }

    override fun onBindViewHolder(holder: PastBirthdayViewHolder, position: Int) {
        val birthday = pastBirthdayList[position]
        holder.nameTextView.text = birthday.name
        holder.birthdayDateTextView.text = when (preferences.getString(LANGUAGE_KEY, null)) {
            "en" -> "Memories from ${birthday.birthdayDate}, a year older and wiser."
            "tr" -> "Doğum Tarihi: ${birthday.birthdayDate}"
            else -> "Memories from ${birthday.birthdayDate}, a year older and wiser."
        }
     }

    override fun getItemCount(): Int {
        return pastBirthdayList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun updateData(newBirthdays: List<Birthday>) {
        //textView.visibility = if (newBirthdays.isEmpty()) View.VISIBLE else View.INVISIBLE
        pastBirthdayList = newBirthdays
        notifyDataSetChanged()
    }


}