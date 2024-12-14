package com.yargisoft.birthify.ui.adapters

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
import com.yargisoft.birthify.data.models.Birthday
import com.yargisoft.birthify.utils.sharedpreferences.UserConstants.LANGUAGE_KEY
import com.yargisoft.birthify.utils.sharedpreferences.UserConstants.PREFS_SETTINGS
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class BirthdayAdapter @Inject constructor(
    @ApplicationContext val context: Context,
) : RecyclerView.Adapter<BirthdayAdapter.BirthdayViewHolder>(), AdapterInterface {

    private var birthdayList: List<Birthday> = emptyList()
    private lateinit var onEditClick: (Birthday) -> Unit
    private lateinit var onDetailClick: (Birthday) -> Unit
    private val preferences: SharedPreferences = context.getSharedPreferences(PREFS_SETTINGS, Context.MODE_PRIVATE)

    class BirthdayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nameCardTv)
        val birthdayDateTextView: TextView = itemView.findViewById(R.id.birthdayDateCardTv)
        val editButton: ImageView = itemView.findViewById(R.id.editBirthdayButtonMainPage)
        val toDetailView: CardView = itemView.findViewById(R.id.birthdayCardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BirthdayViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_birthday, parent, false)
        return BirthdayViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: BirthdayViewHolder, position: Int) {
        val birthday = birthdayList[position]
        holder.nameTextView.text = birthday.name
        holder.birthdayDateTextView.text = when (preferences.getString(LANGUAGE_KEY, null)) {
            "en" -> "A new chapter begins on ${birthday.birthdayDate}!"
            "tr" -> "Doğum Tarihi: ${birthday.birthdayDate}"
            else -> "A fresh start on " + birthday.birthdayDate + " awaits!"
        }

        holder.editButton.setOnClickListener { onEditClick(birthday) }
        holder.toDetailView.setOnClickListener { onDetailClick(birthday) }
    }

    override fun getItemCount(): Int {
        return birthdayList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun updateData(newBirthdays: List<Birthday>) {
        //clickToAddTextView.visibility = if (newBirthdays.isEmpty()) View.VISIBLE else View.INVISIBLE
        birthdayList = newBirthdays
        notifyDataSetChanged()
    }

    fun setOnEditClickListener(listener: (Birthday) -> Unit) {
        onEditClick = listener
    }

    fun setOnDetailClickListener(listener: (Birthday) -> Unit) {
        onDetailClick = listener
    }
}