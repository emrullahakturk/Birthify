package com.yargisoft.birthify.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.yargisoft.birthify.R
import com.yargisoft.birthify.models.Birthday
import com.yargisoft.birthify.viewmodels.BirthdayViewModel

class DeletedBrithdayAdapter(private var deletedBirthdayList: List<Birthday>,
                      private val onDetailClick: (Birthday) -> Unit,
                      val context : Context,
                      private val viewModel: BirthdayViewModel,
) : RecyclerView.Adapter<DeletedBrithdayAdapter.DeletedBirthdayViewHolder>() {



    class DeletedBirthdayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nameCardTv)
        val birthdayDateTextView: TextView = itemView.findViewById(R.id.birthdayDateCardTv)
        val noteTextView: TextView = itemView.findViewById(R.id.noteCardTv)
        val toDetailView: CardView = itemView.findViewById(R.id.birthdayCardView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeletedBirthdayViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.deleted_birthday_item, parent, false)

        return DeletedBirthdayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeletedBirthdayViewHolder, position: Int) {
        val birthday = deletedBirthdayList[position]
        holder.nameTextView.text = birthday.name
        holder.birthdayDateTextView.text = birthday.birthdayDate
        holder.noteTextView.text = birthday.note
        holder.toDetailView.setOnClickListener{
            onDetailClick(birthday)
        }

    }

    override fun getItemCount(): Int {
        return deletedBirthdayList.size
    }





}
