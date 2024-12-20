package com.yargisoft.birthify.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.yargisoft.birthify.R
import com.yargisoft.birthify.data.models.Birthday

class DeletedBirthdayAdapter : RecyclerView.Adapter<DeletedBirthdayListViewHolder>(), AdapterInterface {
    var deletedBirthdayList: List<Birthday> = emptyList()
    private lateinit var onDetailClick: (Birthday) -> Unit
    var languageInfo: String = "en"

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DeletedBirthdayListViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_deleted_birthday, parent, false)
        return DeletedBirthdayListViewHolder(view)
    }

    override fun getItemCount(): Int {
       return deletedBirthdayList.size
    }

    override fun onBindViewHolder(holder: DeletedBirthdayListViewHolder, position: Int) {
       val birthday = deletedBirthdayList[position]
       holder.bind(birthday, onDetailClick, languageInfo)
    }
    @SuppressLint("NotifyDataSetChanged")
    override fun updateData(newBirthdays: List<Birthday>) {
        //textView.visibility = if (newBirthdays.isEmpty()) View.VISIBLE else View.INVISIBLE
        deletedBirthdayList = newBirthdays
        notifyDataSetChanged()
    }

    fun setOnDetailClickListener(listener: (Birthday) -> Unit) {
        onDetailClick = listener
    }
}