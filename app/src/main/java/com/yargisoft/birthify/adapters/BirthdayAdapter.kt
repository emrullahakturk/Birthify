package com.yargisoft.birthify.adapters

import android.animation.Animator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.snackbar.Snackbar
import com.yargisoft.birthify.R
import com.yargisoft.birthify.models.Birthday
import com.yargisoft.birthify.sharedpreferences.SharedPreferencesManager
import com.yargisoft.birthify.viewmodels.BirthdayViewModel

class BirthdayAdapter(private var birthdayList: List<Birthday>,
                      private val onEditClick: (Birthday) -> Unit,
                      private val onDetailClick: (Birthday) -> Unit,
                      val context : Context,
                      private val viewModel: BirthdayViewModel,
                      private val lifeCycleOwnner:LifecycleOwner,
                      private val lottieAnimationView: LottieAnimationView,
                      private val progressBar: ProgressBar,
                      private val topLayout: ConstraintLayout
    ) : RecyclerView.Adapter<BirthdayAdapter.BirthdayViewHolder>() {



    class BirthdayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nameCardTv)
        val birthdayDateTextView: TextView = itemView.findViewById(R.id.birthdayDateCardTv)
        val noteTextView: TextView = itemView.findViewById(R.id.noteCardTv)
        val editButton: ImageView = itemView.findViewById(R.id.editBirthdayButtonMainPage)
        val toDetailView: CardView = itemView.findViewById(R.id.birthdayCardView)
    }

    private  val preferences = SharedPreferencesManager(context)


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

    @SuppressLint("ShowToast")
    fun showDeleteDialog(position: Int){
        Log.e("silme","silme işlemi")
        val view = (context as Activity).findViewById<View>(android.R.id.content)
        Log.e("silme","${birthdayList}")
        if (birthdayList.isNotEmpty()){
           val birthday = birthdayList[position]
            Log.e("silme","silme işlemi dialog")
            AlertDialog.Builder(context)
                .setTitle("Delete Birthday")
                .setMessage("Are you sure you want to delete this birthday?")
                .setPositiveButton("Yes") { _, _ ->


                    progressBar.visibility = View.VISIBLE
                    topLayout.isEnabled = false
                    // Snackbar oluştur
                    val snackbar = Snackbar.make(view, "Deleting birthday...", Snackbar.LENGTH_LONG)
                    snackbar.setAction("Undo") {
                        progressBar.visibility = View.INVISIBLE
                        topLayout.isEnabled = true
                        // Undo butonuna tıklanırsa, öğeyi geri ekleyin
                        viewModel.getUserBirthdays(preferences.getUserId())
                    }

                    // Snackbar'ın görsel düzenini değiştirme
                    snackbar.setActionTextColor(Color.YELLOW)
                    snackbar.setTextColor(Color.WHITE)

                    snackbar.addCallback(object : Snackbar.Callback() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            if (event != DISMISS_EVENT_ACTION) {
                                // Silme işlemini yap
                                progressBar.visibility = View.VISIBLE
                                topLayout.isEnabled = false

                                viewModel.deleteBirthday(birthday.id,birthday)
                                viewModel.deleteBirthdayState.observe(lifeCycleOwnner, Observer { isSuccess->
                                    if(isSuccess){
                                       progressBar.visibility = View.INVISIBLE
                                        topLayout.isEnabled = true

                                        // Show Lottie animation
                                        lottieAnimationView.visibility = View.VISIBLE
                                        lottieAnimationView.playAnimation()
                                        // Wait for animation to finish before deleting item
                                        lottieAnimationView.addAnimatorListener(object : Animator.AnimatorListener {
                                            override fun onAnimationStart(animation: Animator) {}
                                            override fun onAnimationEnd(animation: Animator) {
                                                lottieAnimationView.visibility = View.GONE
                                                Snackbar.make(view, "Birthday successfully deleted", Snackbar.LENGTH_LONG) .show()
                                            }
                                            override fun onAnimationCancel(animation: Animator) {}
                                            override fun onAnimationRepeat(animation: Animator) {}
                                        })

                                    }else{
                                       progressBar.visibility = View.INVISIBLE
                                        topLayout.isEnabled = true

                                        Snackbar.make(view, "Birthday deleting failed", Snackbar.LENGTH_LONG).show()
                                        notifyItemChanged(position)
                                    }
                                })
                                viewModel.getUserBirthdays(preferences.getUserId())
                            }
                        }
                    })

                    snackbar.show()





                }
                .setNegativeButton("No") { _, _ ->
                    // Eğer silme iptal edilirse öğeyi geri yerleştir
                    viewModel.getUserBirthdays(preferences.getUserId())
                }
                .show()
        }
    }




    }
