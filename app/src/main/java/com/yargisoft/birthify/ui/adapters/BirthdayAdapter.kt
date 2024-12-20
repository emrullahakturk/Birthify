package com.yargisoft.birthify.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yargisoft.birthify.R
import com.yargisoft.birthify.data.models.Birthday
import javax.inject.Inject

class BirthdayAdapter @Inject constructor() :
    RecyclerView.Adapter<BirthdayListViewHolder>(), AdapterInterface {
    var birthdayList: List<Birthday> = emptyList()
    var languageInfo: String = "en"
    private lateinit var onEditClick: (Birthday) -> Unit
    private lateinit var onDetailClick: (Birthday) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BirthdayListViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_birthday, parent, false)
        return BirthdayListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return birthdayList.size
    }

    override fun onBindViewHolder(holder: BirthdayListViewHolder, position: Int) {
        val birthday = birthdayList[position]
        holder.bind(birthday, onEditClick, onDetailClick, languageInfo)
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