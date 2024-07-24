package com.yargisoft.birthify.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.snackbar.Snackbar
import com.yargisoft.birthify.R
import com.yargisoft.birthify.models.Birthday
import com.yargisoft.birthify.sharedpreferences.UserSharedPreferencesManager
import com.yargisoft.birthify.viewmodels.BirthdayViewModel
import kotlinx.coroutines.launch

class BirthdayAdapter(private var birthdayList: List<Birthday>,
                      private val onEditClick: (Birthday) -> Unit,
                      private val onDetailClick: (Birthday) -> Unit,
                      val context : Context,
                      private val viewModel: BirthdayViewModel,
                      private val lifeCycleOwner:LifecycleOwner,
                      private val deleteLottieAnimationView: LottieAnimationView,
                      private val threePointLottieAnimationView: LottieAnimationView,
                      private val fragmentView: View,
                      private val clickToAddTextView: TextView
    ) : RecyclerView.Adapter<BirthdayAdapter.BirthdayViewHolder>() {


        private  val userPreferences = UserSharedPreferencesManager(context)



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
            holder.editButton.setOnClickListener {onEditClick(birthday)}
            holder.toDetailView.setOnClickListener{onDetailClick(birthday)}
        }
        override fun getItemCount(): Int {return birthdayList.size}

        @SuppressLint("NotifyDataSetChanged")
        fun updateData(newBirthdayList: List<Birthday>) {
            clickToAddTextView.visibility = if (newBirthdayList.isEmpty()) View.VISIBLE else View.INVISIBLE
            birthdayList = newBirthdayList
            notifyDataSetChanged()
        }

        @SuppressLint("NotifyDataSetChanged")
        fun showDeleteDialog(position: Int){
            val view = (context as Activity).findViewById<View>(android.R.id.content)
            if (birthdayList.isNotEmpty()){
               val birthday = birthdayList[position]
                AlertDialog.Builder(context)
                    .setTitle("Delete Birthday")
                    .setMessage("Are you sure you want to delete this birthday?")
                    .setPositiveButton("Yes") { _, _ ->

                        deleteLottieAnimationView.visibility = View.VISIBLE
                        deleteLottieAnimationView.playAnimation()
                        threePointLottieAnimationView.visibility = View.VISIBLE
                        threePointLottieAnimationView.playAnimation()
                        setViewAndChildrenEnabled(fragmentView, false)

                        // Silme işlemini yap
                        viewModel.deleteBirthday(birthday.id,birthday)

                        Handler(Looper.getMainLooper()).postDelayed({
                            lifeCycleOwner.lifecycleScope.launch {
                                lifeCycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                                    viewModel.deleteBirthdayState.collect{isDeleted ->
                                        if (isDeleted){
                                            viewModel.getUserBirthdays(userPreferences.getUserId())
                                            setViewAndChildrenEnabled(fragmentView, true)
                                            deleteLottieAnimationView.visibility = View.INVISIBLE
                                            deleteLottieAnimationView.cancelAnimation()
                                            threePointLottieAnimationView.visibility = View.INVISIBLE
                                            threePointLottieAnimationView.cancelAnimation()
                                            Snackbar.make(view, "Birthday successfully deleted", Snackbar.LENGTH_LONG).show()
                                        }else{
                                            Snackbar.make(view, "Birthday deleting failed", Snackbar.LENGTH_LONG).show()
                                        }

                                    }
                                }
                            }
                        },2000)

                    }
                    .setNegativeButton("No") { _, _ ->
                        // Eğer silme iptal edilirse öğeyi geri yerleştir
                        viewModel.getUserBirthdays(userPreferences.getUserId())
                    }
                    .show()
            }
        }
        private fun setViewAndChildrenEnabled(view: View, enabled: Boolean) {
        view.isEnabled = enabled
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                setViewAndChildrenEnabled(view.getChildAt(i), enabled)
            }
        }
    }

    }