package com.yargisoft.birthify.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yargisoft.birthify.R
import com.yargisoft.birthify.data.models.Birthday

class PastBirthdayAdapter : RecyclerView.Adapter<PastBirthdayListViewHolder>(), AdapterInterface {

    var pastBirthdayList: List<Birthday> = emptyList()
    var languageInfo: String = "en"


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PastBirthdayListViewHolder {
    val view = LayoutInflater.from(parent.context).inflate(R.layout.item_past_birthday, parent, false)
    return PastBirthdayListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return pastBirthdayList.size
    }

    override fun onBindViewHolder(holder: PastBirthdayListViewHolder, position: Int) {
        holder.bind(pastBirthdayList[position], languageInfo)
    }
    @SuppressLint("NotifyDataSetChanged")
    override fun updateData(newBirthdays: List<Birthday>) {
        //textView.visibility = if (newBirthdays.isEmpty()) View.VISIBLE else View.INVISIBLE
        pastBirthdayList = newBirthdays
        notifyDataSetChanged()
    }
}