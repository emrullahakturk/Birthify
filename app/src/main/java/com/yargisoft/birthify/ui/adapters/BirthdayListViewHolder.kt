package com.yargisoft.birthify.ui.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.yargisoft.birthify.data.models.Birthday
import com.yargisoft.birthify.databinding.ItemBirthdayBinding

class BirthdayListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val binding = ItemBirthdayBinding.bind(itemView)
    inline fun bind(
        birthday: Birthday,
        crossinline onClickEdit: (Birthday) -> Unit,
        crossinline onClickDetail: (Birthday) -> Unit,
        languageInfo:String
    ) {
        binding.nameCardTv.text = birthday.name
        binding.birthdayDateCardTv.text = when (languageInfo) {
            "en" -> "A new chapter begins on ${birthday.birthdayDate}!"
            "tr" -> "Doğum Tarihi: ${birthday.birthdayDate}"
            else -> "A fresh start on " + birthday.birthdayDate + " awaits!"
        }
        binding.editBirthdayButtonMainPage.setOnClickListener {
            onClickEdit.invoke(birthday)
        }
        binding.birthdayCardView.setOnClickListener {
            onClickDetail.invoke(birthday)
        }
    }
}