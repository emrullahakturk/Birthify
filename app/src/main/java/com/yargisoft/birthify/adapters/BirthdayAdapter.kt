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

class BirthdayAdapter(private var birthdayList: List<Birthday>,
                      private val onEditClick: (Birthday) -> Unit,
                      private val onDetailClick: (Birthday) -> Unit,
                      val context : Context,
                      private val viewModel: BirthdayViewModel,
    ) : RecyclerView.Adapter<BirthdayAdapter.BirthdayViewHolder>() {



    class BirthdayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nameCardTv)
        val birthdayDateTextView: TextView = itemView.findViewById(R.id.birthdayDateCardTv)
        val noteTextView: TextView = itemView.findViewById(R.id.noteCardTv)
        val editButton: ImageView = itemView.findViewById(R.id.editBirthdayButtonMainPage)
        val toDetailView: CardView = itemView.findViewById(R.id.birthdayCardView)
    }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BirthdayViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.birthday_item, parent, false)

            return BirthdayViewHolder(view)
        }

        override fun onBindViewHolder(holder: BirthdayViewHolder, position: Int) {
            val birthday = birthdayList[position]
            holder.nameTextView.text = birthday.name
            holder.birthdayDateTextView.text = birthday.birthdayDate
            holder.noteTextView.text = birthday.note
            holder.editButton.setOnClickListener {
                onEditClick(birthday)
            }
            holder.toDetailView.setOnClickListener{
                onDetailClick(birthday)
            }

        }

        override fun getItemCount(): Int {
            return birthdayList.size
        }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newBirthdayList: List<Birthday>) {
        birthdayList = newBirthdayList
        notifyDataSetChanged()
    }

     /*   fun showDeleteDialog(position: Int){
            if (birthdayList.isEmpty()){
               val birthday = birthdayList[position]
                Toast.makeText(context, "Showing delete confirmation", Toast.LENGTH_SHORT).show()
                AlertDialog.Builder(context)
                    .setTitle("Delete Birthday")
                    .setMessage("Are you sure you want to delete this birthday?")
                    .setPositiveButton("Yes") { dialog, which ->
                        viewModel.deleteBirthday(birthday.id) { isSuccess ->
                            if (isSuccess) {
                                Toast.makeText(context, "Birthday deleted successfully", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Failed to delete birthday", Toast.LENGTH_SHORT).show()
                            }
                        }

                    }
                    .setNegativeButton("No") { dialog, which ->
                        // Eğer silme iptal edilirse öğeyi geri yerleştir
                        notifyItemChanged(position)
                    }
                    .show()
            }
        }*/


    }
