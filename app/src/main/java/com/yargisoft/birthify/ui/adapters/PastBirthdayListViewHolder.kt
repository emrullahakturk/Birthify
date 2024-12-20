package com.yargisoft.birthify.ui.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.yargisoft.birthify.data.models.Birthday
import com.yargisoft.birthify.databinding.ItemPastBirthdayBinding

class PastBirthdayListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val binding: ItemPastBirthdayBinding = ItemPastBirthdayBinding.bind(itemView)

    fun bind(birthday: Birthday, languageInfo: String) {
        binding.nameCardTv.text = birthday.name
        binding.birthdayDateCardTv.text = when (languageInfo) {
            "en" -> "Memories from ${birthday.birthdayDate}, a year older and wiser."
            "tr" -> "Doğum Tarihi: ${birthday.birthdayDate}"
            else -> "Memories from ${birthday.birthdayDate}, a year older and wiser."
        }
    }

}