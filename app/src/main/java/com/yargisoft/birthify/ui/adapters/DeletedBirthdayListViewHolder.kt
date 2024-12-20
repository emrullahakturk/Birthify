package com.yargisoft.birthify.ui.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.yargisoft.birthify.data.models.Birthday
import com.yargisoft.birthify.databinding.ItemDeletedBirthdayBinding

class DeletedBirthdayListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    val binding = ItemDeletedBirthdayBinding.bind(itemView)
    fun bind(birthday: Birthday, onClickDetail :(birthday: Birthday) -> Unit , languageInfo:String){
        binding.nameCardTv.text = birthday.name
        binding.birthdayDateCardTv.text =  when (languageInfo) {
            "en" -> "Birthday Date: ${birthday.birthdayDate}"
            "tr" -> "Doğum Tarihi: ${birthday.birthdayDate}"
            else -> "Birthday Date: ${birthday.birthdayDate}"
        }
        binding.birthdayCardView.setOnClickListener {
            onClickDetail.invoke(birthday)
        }
    }
}