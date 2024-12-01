package com.yargisoft.birthify.ui.adapters

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.yargisoft.birthify.R
import com.yargisoft.birthify.data.models.Birthday
import javax.inject.Inject


class DeletedBirthdayAdapter(private var deletedBirthdayList: List<Birthday>,
                             private val onDetailClick: (Birthday) -> Unit,
                             private val textView: TextView
) : RecyclerView.Adapter<DeletedBirthdayAdapter.DeletedBirthdayViewHolder>() , AdapterInterface{

    @Inject
    lateinit var prefAppSettings: SharedPreferences


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
        holder.birthdayDateTextView.text = when (prefAppSettings.getString("AppLanguage", null)) {
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
    override fun updateData(newBirthdays: List<Birthday>) {
        textView.visibility = if (newBirthdays.isEmpty()) View.VISIBLE else View.INVISIBLE
        deletedBirthdayList = newBirthdays
        notifyDataSetChanged()
    }



}
