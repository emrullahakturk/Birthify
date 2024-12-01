package com.yargisoft.birthify.ui.adapters

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
import com.yargisoft.birthify.data.models.Birthday
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PastBirthdayAdapter @Inject constructor(
    private val prefAppSettings: SharedPreferences,
    @ApplicationContext private val context: Context,
    private val textView: TextView
) : RecyclerView.Adapter<PastBirthdayAdapter.PastBirthdayViewHolder>(), AdapterInterface {

    private var pastBirthdayList: List<Birthday> = emptyList()
    private lateinit var onDetailClick: (Birthday) -> Unit

    class PastBirthdayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nameCardTv)
        val birthdayDateTextView: TextView = itemView.findViewById(R.id.birthdayDateCardTv)
        val toDetailView: CardView = itemView.findViewById(R.id.birthdayCardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PastBirthdayViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_past_birthday, parent, false)
        return PastBirthdayViewHolder(view)
    }

    override fun onBindViewHolder(holder: PastBirthdayViewHolder, position: Int) {
        val birthday = pastBirthdayList[position]
        holder.nameTextView.text = birthday.name
        holder.birthdayDateTextView.text = when (prefAppSettings.getString("AppLanguage", null)) {
            "en" -> "Memories from ${birthday.birthdayDate}, a year older and wiser."
            "tr" -> "Doğum Tarihi: ${birthday.birthdayDate}"
            else -> "Memories from ${birthday.birthdayDate}, a year older and wiser."
        }
        holder.toDetailView.setOnClickListener { onDetailClick(birthday) }
    }

    override fun getItemCount(): Int {
        return pastBirthdayList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun updateData(newBirthdays: List<Birthday>) {
        textView.visibility = if (newBirthdays.isEmpty()) View.VISIBLE else View.INVISIBLE
        pastBirthdayList = newBirthdays
        notifyDataSetChanged()
    }

    fun setOnDetailClickListener(listener: (Birthday) -> Unit) {
        onDetailClick = listener
    }
}