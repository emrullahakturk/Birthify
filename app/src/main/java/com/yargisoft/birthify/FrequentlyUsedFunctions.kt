package com.yargisoft.birthify

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.textfield.TextInputEditText
import com.yargisoft.birthify.data.models.Birthday
import com.yargisoft.birthify.ui.adapters.AdapterInterface
import com.yargisoft.birthify.ui.viewmodels.BirthdayViewModel
import com.yargisoft.birthify.utils.reminder.ReminderFunctions.cancelBirthdayReminder
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


object FrequentlyUsedFunctions {


    //EditText ile arama yaparken aramayı filtrelemek için kullanılan fonksiyon (PastBirthday İçin)
    fun filterBirthdays(query: String, viewModel: BirthdayViewModel, adapter: AdapterInterface) {
        val birthdays = viewModel.birthdayList.value
        if (birthdays != null) {
            val filteredBirthdays = if (query.isEmpty()) {
                birthdays
            } else {
                birthdays.filter { it.name.contains(query, ignoreCase = true) }
            }
            adapter.updateData(filteredBirthdays)
        }
    }


    //Ekranı tıklanabilir hale getiren ve lottie animasyonunu durduran fonksiyon
    fun enableViewDisableLottie(lottieAnimationView: LottieAnimationView, view: View) {
        lottieAnimationView.cancelAnimation()
        lottieAnimationView.visibility = View.INVISIBLE
        setViewAndChildrenEnabled(view, true)
    }

    //Ekranı tıklanamaz hale getiren ve lottie animasyonunu başlatan fonksiyon
    fun disableViewEnableLottie(lottieAnimationView: LottieAnimationView, view: View) {
        lottieAnimationView.visibility = View.VISIBLE
        lottieAnimationView.playAnimation()
        setViewAndChildrenEnabled(view, false)
    }

    // Tarih seçme ekranı için gereken fonksiyon
    @SuppressLint("DiscouragedApi")
    fun showDatePickerDialog(context: Context, editText: TextInputEditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(context, { _, _, selectedMonth, selectedDay ->
            val monthFormat = SimpleDateFormat("MMMM", Locale.US)
            val monthName =
                monthFormat.format(calendar.apply { set(Calendar.MONTH, selectedMonth) }.time)
            val selectedDate = "$selectedDay $monthName"
            editText.setText(selectedDate)
        }, year, month, day)

        // Yıl seçimini kapatma
        datePickerDialog.datePicker.findViewById<View>(
            context.resources.getIdentifier(
                "year",
                "id",
                "android"
            )
        )?.visibility = View.GONE

        datePickerDialog.show()
    }


    //parametre olarak gelen view'i devre dışı bırakmak ve tekrar aktif etmek için kullanılan fonksiyon
    private fun setViewAndChildrenEnabled(view: View, enabled: Boolean) {
        view.isEnabled = enabled
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                setViewAndChildrenEnabled(view.getChildAt(i), enabled)
            }
        }
    }
    //parametre olarak gelen view'i devre dışı bırakmak ve tekrar aktif etmek için kullanılan fonksiyon

    /*
        silme , yeniden kaydetme ve tamamen silme işlemlerimiz
        için kullandığımız onaylama diyaloğumu çağıran fonksiyon
    */
    fun showConfirmationDialog(
        view: View,
        context: Context,
        birthdayViewModel: BirthdayViewModel,
        editedBirthday: Birthday,
        lottieAnimationView: LottieAnimationView,
        viewLifecycleOwner: LifecycleOwner,
        condition: String,
        findNavController: NavController,
        action: Int,
        navOptions: NavOptions
    ) {
        AlertDialog.Builder(context)
            .setTitle(getString(context, R.string.confirm_operation_title))
            .setMessage(getString(context, R.string.confirm_operation))
            .setPositiveButton(getString(context, R.string.yes)) { _, _ ->

                when (condition) {
                    "permanently" -> {
                        birthdayViewModel.permanentlyDeleteBirthday(editedBirthday.id)
                    }

                    "soft_delete" -> {
                        birthdayViewModel.deleteBirthday(editedBirthday.id)
                        cancelBirthdayReminder(
                            editedBirthday.id,
                            editedBirthday.name,
                            editedBirthday.birthdayDate,
                            context
                        )

                    }

                    "re_save" -> {
                        birthdayViewModel.reSaveDeletedBirthday(editedBirthday.id)
                    }
                }

                loadAndStateOperation(
                    viewLifecycleOwner,
                    birthdayViewModel,
                    lottieAnimationView,
                    view,
                    findNavController,
                    action,
                    navOptions
                )
            }
            .setNegativeButton(getString(context, R.string.no)) { _, _ ->
                //animasyonu durdurup view'i visible yapıyoruz
                enableViewDisableLottie(lottieAnimationView, view)
            }
            .show()
    }


    //Swipe ederek silme işlemi yaparken UserSwipeToDeleteCallback sınıfından bu fonksiyon çağrılır ve silme işlemi başlatılır
    @SuppressLint("NotifyDataSetChanged")
    fun showDeleteDialogBirthdayAdapter(
        position: Int,
        view: View,
        context: Context,
        birthdayList: List<Birthday>,
        lottieAnimationView: LottieAnimationView,
        birthdayViewModel: BirthdayViewModel,
        lifeCycleOwner: LifecycleOwner,
        findNavController: NavController,
        action: Int,
        navOptions: NavOptions
    ) {
        if (birthdayList.isNotEmpty()) {
            val birthday = birthdayList[position]
            showConfirmationDialog(
                view,
                context,
                birthdayViewModel,
                birthday,
                lottieAnimationView,
                lifeCycleOwner,
                "soft_delete",
                findNavController,
                action,
                navOptions
            )
        }
    }


    //Yaptığımız suspend işlemlerin tamamlanıp tamamlanmadığını kontrol eden fonksiyon
    // aynı zamanda arayüzü kilitleyip kullanıcının ekranda işlemler yapmasını engelliyor (disableViewEnableLottie ile)
    //isloading işlemin tamamlanıp tamamlanmadığını dönderiyor ve içerisindeki enableViewDisableLottie fonksiyonu ile arayüzü
    //kullanıcının kullanımına açıyor
    fun loadAndStateOperation(
        viewLifecycleOwner: LifecycleOwner,
        birthdayViewModel: BirthdayViewModel,
        lottieAnimationView: LottieAnimationView,
        view: View,
        findNavController: NavController,
        action: Int,
        navOptions: NavOptions
    ) {
        disableViewEnableLottie(lottieAnimationView, view)
        isLoadingCheck(
            viewLifecycleOwner,
            birthdayViewModel,
            lottieAnimationView,
            view,
            findNavController,
            action,
            navOptions
        )
    }


    //Auth View Model ve UsersBirthdayViewModel için isLoading Kontrolü yapan fonksiyon
    // (Suspend fonksiyonun bitip bitmediğini kontrol ediyoruz)
    private fun isLoadingCheck(
        viewLifecycleOwner: LifecycleOwner,
        viewModel: ViewModel,
        lottieAnimationView: LottieAnimationView,
        view: View,
        findNavController: NavController?,
        action: Int?,
        navOptions: NavOptions
    ) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

/*
                if(viewModel is AuthViewModel){ //gelen viewModel Authviewmodel ise bu çalışacak
                        if(viewModel.isLoaded == true){

                            if(viewModel.authSuccess.value==true){
                                Snackbar.make(view,"Successful",Snackbar.LENGTH_SHORT).show()
                            }
                            else{
                                Snackbar.make(view,"${viewModel.authError.value}",Snackbar.LENGTH_SHORT).show()
                            }
                            //animasyonu durdurup view'i visible yapıyoruz
                            enableViewDisableLottie(lottieAnimationView,view)
                            if (findNavController != null && action != null){
                                findNavController.navigate(action,null, navOptions )
                            }
                        }

                }
*/

                if (viewModel is BirthdayViewModel) {  //gelen viewModel Birthday viewmodel ise bu çalışacak
                    Handler(Looper.getMainLooper()).postDelayed({
                        //animasyonu durdurup view'i visible yapıyoruz
                        enableViewDisableLottie(lottieAnimationView, view)
                        if (findNavController != null && action != null) {
                            findNavController.navigate(action, null, navOptions)
                        }
                    }, 1500)
                }
            }
        }
    }



    fun navigateToFragmentAndClearStack(
        navController: NavController,
        currentFragmentId: Int,
        targetFragmentId: Int
    ) {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(currentFragmentId, inclusive = true)
            .build()

        navController.navigate(targetFragmentId, null, navOptions)
    }

    // Logout fonksiyonu
    fun logout(activity: Activity) {
        // Mevcut aktiviteyi kapatır ve yeni bir aktivite başlatır yani uygulama sıfırdan başlamış gibi olur
        val intent = Intent(activity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        activity.startActivity(intent)
        activity.finish()
    }


}