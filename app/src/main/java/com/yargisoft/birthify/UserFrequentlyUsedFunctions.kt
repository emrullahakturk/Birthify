@file:Suppress("UNUSED_EXPRESSION")

package com.yargisoft.birthify

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.util.Patterns
import android.view.ContextThemeWrapper
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.yargisoft.birthify.adapters.BirthdayAdapter
import com.yargisoft.birthify.adapters.DeletedBirthdayAdapter
import com.yargisoft.birthify.models.Birthday
import com.yargisoft.birthify.repositories.BirthdayRepository
import com.yargisoft.birthify.sharedpreferences.UserSharedPreferencesManager
import com.yargisoft.birthify.viewmodels.AuthViewModel
import com.yargisoft.birthify.viewmodels.UsersBirthdayViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


object UserFrequentlyUsedFunctions {

    //EditText ile arama yaparken aramayı filtrelemek için kullanılan fonksiyon
     fun filterBirthdays(query: String, viewModel:UsersBirthdayViewModel, adapter: DeletedBirthdayAdapter) {
        val birthdays = viewModel.deletedBirthdayList.value
        if (birthdays != null) {
            val filteredBirthdays = if (query.isEmpty()) {
                birthdays
            } else {
                birthdays.filter { it.name.contains(query, ignoreCase = true) }
            }
            adapter.updateData(filteredBirthdays)
        }
    }

    //EditText ile arama yaparken aramayı filtrelemek için kullanılan fonksiyon
     fun filterBirthdays(query: String, viewModel:UsersBirthdayViewModel, adapter: BirthdayAdapter) {
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
    private fun enableViewDisableLottie(lottieAnimationView: LottieAnimationView, view: View)
    {
        lottieAnimationView.cancelAnimation()
        lottieAnimationView.visibility = View.INVISIBLE
        setViewAndChildrenEnabled(view,true)
    }

    //Ekranı tıklanamaz hale getiren ve lottie animasyonunu başlatan fonksiyon
    private fun disableViewEnableLottie(lottieAnimationView:LottieAnimationView, view: View)
    {
        lottieAnimationView.visibility = View.VISIBLE
        lottieAnimationView.playAnimation()
        setViewAndChildrenEnabled(view, false)
    }

    //Main Page için sort menüsünü açma fonksiyonları
        fun showSortMenu(view: View,
                         context:Context,
                         adapter: BirthdayAdapter,
                         usersBirthdayViewModel: UsersBirthdayViewModel){

                val contextThemeWrapper = ContextThemeWrapper(context, R.style.CustomPopupMenu)
                val popupMenu = PopupMenu(contextThemeWrapper, view)
                popupMenu.menuInflater.inflate(R.menu.sorting_birthday_menu, popupMenu.menu)

                // Menü öğelerine özel stil uygulama
                for (i in 0 until popupMenu.menu.size()) {
                    val menuItem = popupMenu.menu.getItem(i)
                    val spanString = SpannableString(menuItem.title)
                    spanString.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.green_login)), 0, spanString.length, 0)
                    menuItem.title = spanString
                }

                popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
                    handleSortOptionSelected(menuItem,adapter,usersBirthdayViewModel)
                    true
                }
                popupMenu.show()

        }
        private fun handleSortOptionSelected(menuItem: MenuItem,
                                             adapter: BirthdayAdapter,
                                             usersBirthdayViewModel: UsersBirthdayViewModel) {

                val sortedList = when (menuItem.itemId) {
                    R.id.sort_by_name_asc -> usersBirthdayViewModel.sortBirthdaysMainPage("sortBirthdaysByNameAsc")
                    R.id.sort_by_birth_date_asc -> usersBirthdayViewModel.sortBirthdaysMainPage("sortBirthdaysByBirthdayDateAsc")
                    R.id.sort_by_recorded_date_asc -> usersBirthdayViewModel.sortBirthdaysMainPage("sortBirthdaysByRecordedDateAsc")
                    R.id.sort_by_name_dsc -> usersBirthdayViewModel.sortBirthdaysMainPage("sortBirthdaysByNameDsc")
                    R.id.sort_by_birth_date_dsc -> usersBirthdayViewModel.sortBirthdaysMainPage("sortBirthdaysByBirthdayDateDsc")
                    R.id.sort_by_recorded_date_dsc -> usersBirthdayViewModel.sortBirthdaysMainPage("sortBirthdaysByRecordedDateDsc")
                    else -> emptyList()
                }

                adapter.updateData(sortedList)
         }
    //Main Page için sort menüsünü açma fonksiyonları


    // Deleted (Trash Bin) Page için sort menüsünü açma fonksiyonları
        fun showSortMenu(view: View, context:Context, adapter: DeletedBirthdayAdapter, usersBirthdayViewModel: UsersBirthdayViewModel) {
        val contextThemeWrapper = ContextThemeWrapper(context, R.style.CustomPopupMenu)
        val popupMenu = PopupMenu(contextThemeWrapper, view)
        popupMenu.menuInflater.inflate(R.menu.sorting_birthday_menu, popupMenu.menu)

        // Menü öğelerine özel stil uygulama
        for (i in 0 until popupMenu.menu.size()) {
            val menuItem = popupMenu.menu.getItem(i)
            val spanString = SpannableString(menuItem.title)
            spanString.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.green_login)), 0, spanString.length, 0)
            menuItem.title = spanString
        }

        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            handleSortOptionSelected(menuItem,adapter,usersBirthdayViewModel)
            true
        }
        popupMenu.show()
    }
        private fun handleSortOptionSelected(menuItem: MenuItem, adapter: DeletedBirthdayAdapter, usersBirthdayViewModel: UsersBirthdayViewModel) {
        val sortedList = when (menuItem.itemId) {
            R.id.sort_by_name_asc -> usersBirthdayViewModel.sortBirthdaysTrashBin("sortBirthdaysByNameAsc")
            R.id.sort_by_birth_date_asc -> usersBirthdayViewModel.sortBirthdaysTrashBin("sortBirthdaysByBirthdayDateAsc")
            R.id.sort_by_recorded_date_asc -> usersBirthdayViewModel.sortBirthdaysTrashBin("sortBirthdaysByRecordedDateAsc")
            R.id.sort_by_name_dsc -> usersBirthdayViewModel.sortBirthdaysTrashBin("sortBirthdaysByNameDsc")
            R.id.sort_by_birth_date_dsc -> usersBirthdayViewModel.sortBirthdaysTrashBin("sortBirthdaysByBirthdayDateDsc")
            R.id.sort_by_recorded_date_dsc -> usersBirthdayViewModel.sortBirthdaysTrashBin("sortBirthdaysByRecordedDateDsc")
            else -> emptyList()
        }

        adapter.updateData(sortedList)
    }
    //Deleted (Trash Bin) Page için sort menüsünü açma fonksiyonları


    // Tarih seçme ekranı için gereken fonksiyon
    @SuppressLint("DiscouragedApi")
    fun showDatePickerDialog(context: Context, editText: TextInputEditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(context, { _, _, selectedMonth, selectedDay ->
            val monthFormat = SimpleDateFormat("MMMM", Locale.US)
            val monthName = monthFormat.format(calendar.apply { set(Calendar.MONTH, selectedMonth) }.time)
            val selectedDate = "$selectedDay $monthName"
            editText.setText(selectedDate)
        }, year, month, day)

        // Yıl seçimini kapatma
        datePickerDialog.datePicker.findViewById<View>(context.resources.getIdentifier("year", "id", "android"))?.visibility = View.GONE

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


    //Email, password ve fullname validation için kullanılan fonksiyonlar
     fun isValidEmail(email: String): Boolean {
        if (email.isBlank()) {
            return false
        }
        val emailPattern = Patterns.EMAIL_ADDRESS
        return emailPattern.matcher(email).matches()
    }
     fun isValidPassword(password: String): Boolean {
        if (password.isBlank()) {
            return false
        }
        val passwordPattern = Regex("^(?=.*[A-Z])(?=.*[0-9])(?=.*\\W)(?=.{6,})\\S*$")
        return passwordPattern.matches(password)
    }
     fun isValidFullName(fullName: String): Boolean {
        // Boş olup olmadığını kontrol et
        if (fullName.isBlank()) {
            return false
        }

        // Kelimeleri ayır
        val words = fullName.trim().split("\\s+".toRegex())

        // En az iki kelime ve her kelimenin en az 3 harf uzunluğunda olması gerekiyor
        if (words.size < 2 || words.any { it.length < 2 }) {
            return false
        }

        // Kelimelerde rakam veya noktalama işareti olmamalı
        val namePattern = Regex("^[a-zA-Z]+$")

        return !words.any { !namePattern.matches(it) }
    }
    //Email, password ve fullname validation için kullanılan fonksiyonlar



/*
    silme , yeniden kaydetme ve tamamen silme işlemlerimiz
    için kullandığımız onaylama diyaloğumu çağıran fonksiyon
*/
    fun showConfirmationDialog(view: View, context: Context, usersBirthdayViewModel:UsersBirthdayViewModel, editedBirthday:Birthday, lottieAnimationView: LottieAnimationView, viewLifecycleOwner:LifecycleOwner, condition: String, findNavController: NavController, action:Int, navOptions: NavOptions )
    {
        AlertDialog.Builder(context)
            .setTitle("Confirm Operation")
            .setMessage("Are you sure you want to do this operation?")
            .setPositiveButton("Yes") { _, _ ->

                when (condition) {
                    "permanently" -> {
                        usersBirthdayViewModel.permanentlyDeleteBirthday(editedBirthday.id)
                    }
                    "soft_delete" -> {
                       usersBirthdayViewModel.deleteBirthday(editedBirthday.id)
                    }
                    "re_save" -> {
                      usersBirthdayViewModel.reSaveDeletedBirthday(editedBirthday.id)
                    }
                }

                loadAndStateOperation(viewLifecycleOwner,usersBirthdayViewModel,lottieAnimationView,view,findNavController,action, navOptions )
            }
            .setNegativeButton("No"){_,_->
                //animasyonu durdurup view'i visible yapıyoruz
                enableViewDisableLottie(lottieAnimationView,view)
            }
            .show()
    }



    /*Login page içerisinde login butonuna tıkladığımızda, verilen email validasyonunu yapan,
       animasyonları gösterip devre dışı bırakan, kullanıcı etkileşimini sağlayan fonksiyon (snackbar iler)*/
    fun loginValidationFunction(view: View,
                                email: String,
                                password:String,
                                isChecked: Boolean,
                                authViewModel: AuthViewModel,
                                lottieAnimationView: LottieAnimationView,
                                viewLifecycleOwner: LifecycleOwner,
                                userSharedPreferences: UserSharedPreferencesManager,
                                findNavController: NavController,
                                action: Int
                                ){

        if (isValidEmail(email) && password.isNotEmpty()) {

            authViewModel.loginUser(email, password,isChecked)

            disableViewEnableLottie(lottieAnimationView,view)

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    authViewModel.isLoaded.collect { isLoaded ->
                        if(isLoaded == true){
                            if(authViewModel.authViewModelState == true){
                                if (authViewModel.isEmailVerifiedResult == true) {
                                    userSharedPreferences.saveIsChecked(isChecked)
                                    Snackbar.make(view, "You successfully logged in", Snackbar.LENGTH_SHORT).show()
                                    navigateToFragmentAndClearStack(findNavController,R.id.loginPageFragment, R.id.loginToMain)

                                } else {
                                    userSharedPreferences.apply {
//                                        clearUserSession()
                                        saveIsChecked(false)
                                    }
                                    Snackbar.make(view, "Please verify your e-mail", Snackbar.LENGTH_LONG).show()
                                    enableViewDisableLottie(lottieAnimationView,view)
                                }
                            }
                            if(authViewModel.authViewModelState == false){
                                Snackbar.make(view,"Login Failed",Snackbar.LENGTH_SHORT).show()
                                enableViewDisableLottie(lottieAnimationView,view)
                            }
                        }
                    }
                }
            }

        }else {
            Snackbar.make(view,"Please fill in all fields",Snackbar.LENGTH_SHORT).show()
        }
    }


    fun registerValidationFunction(email:String, password: String, name:String,
                                   viewModel:AuthViewModel,
                                   lottieAnimationView: LottieAnimationView,
                                   viewLifecycleOwner:LifecycleOwner,
                                   view:View,
                                   findNavController:NavController,
                                   action: Int,
                                   navOptions: NavOptions
                                   ){

        if(isValidPassword(password) && isValidEmail(email) && isValidFullName(name) )
        {

            //user kaydetme fonksiyonunu viewmodeldan çağırıyoruz
            viewModel.registerUser(name,email,password)

            disableViewEnableLottie(lottieAnimationView,view)

            isLoadingCheck(viewLifecycleOwner,viewModel,lottieAnimationView,view,findNavController,action, navOptions )


        }else{
            Snackbar.make(view,"Please correctly fill in all fields",Snackbar.LENGTH_SHORT).show()
        }
    }



    //Swipe ederek silme işlemi yaparken UserSwipeToDeleteCallback sınıfından bu fonksiyon çağrılır ve silme işlemi başlatılır
    @SuppressLint("NotifyDataSetChanged")
    fun showDeleteDialogBirthdayAdapter(position: Int, view: View, context: Context, birthdayList:List<Birthday>, lottieAnimationView: LottieAnimationView, usersBirthdayViewModel: UsersBirthdayViewModel, lifeCycleOwner: LifecycleOwner, findNavController: NavController, action: Int, navOptions: NavOptions
    ){
        Log.e("HATA","$birthdayList")

        if (birthdayList.isNotEmpty()){
            val birthday = birthdayList[position]
            showConfirmationDialog(view,context,usersBirthdayViewModel,birthday,lottieAnimationView,lifeCycleOwner,"soft_delete", findNavController , action, navOptions  )
        }
    }




    /*Şifre sıfırlama ekranında (Forgot Password Page) kutucuğa uazılan mailin validayion işlemlerini,
     animasyon işlemlerini vs yapan fonksiyon
     */
    fun resetPasswordValidation(email:String, viewLifecycleOwner:LifecycleOwner, viewModel:AuthViewModel, lottieAnimationView: LottieAnimationView, view: View, findNavController: NavController, action:Int, navOptions: NavOptions )
    {
        if(isValidEmail(email)){
            viewModel.resetPassword(email)
            disableViewEnableLottie(lottieAnimationView,view)
            isLoadingCheck( viewLifecycleOwner, viewModel, lottieAnimationView, view, findNavController, action, navOptions)
        }

    }



    //Yaptığımız suspend işlemlerin tamamlanıp tamamlanmadığını kontrol eden fonksiyon
    // aynı zamanda arayüzü kilitleyip kullanıcının ekranda işlemler yapmasını engelliyor (disableViewEnableLottie ile)
    //isloading işlemin tamamlanıp tamamlanmadığını dönderiyor ve içerisindeki enableViewDisableLottie fonksiyonu ile arayüzü
    //kullanıcının kullanımına açıyor
    fun loadAndStateOperation(viewLifecycleOwner:LifecycleOwner, usersBirthdayViewModel: UsersBirthdayViewModel, lottieAnimationView: LottieAnimationView, view:View, findNavController: NavController, action:Int , navOptions: NavOptions)
    {
        disableViewEnableLottie(lottieAnimationView,view)
        isLoadingCheck(viewLifecycleOwner,usersBirthdayViewModel,lottieAnimationView,view, findNavController, action, navOptions )
    }


    //Auth View Model ve UsersBirthdayViewModel için isLoading Kontrolü yapan fonksiyon
    // (Suspend fonksiyonun bitip bitmediğini kontrol ediyoruz)
    private fun isLoadingCheck(viewLifecycleOwner: LifecycleOwner, viewModel:ViewModel, lottieAnimationView: LottieAnimationView, view: View, findNavController: NavController?, action: Int?,  navOptions:NavOptions)
    {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                if(viewModel is AuthViewModel){ //gelen viewModel Authviewmodel ise bu çalışacak
                    viewModel.isLoaded.collect { isLoaded ->
                        if(isLoaded == true){

                            if(viewModel.authViewModelState == true){
                                Snackbar.make(view,"Successful",Snackbar.LENGTH_SHORT).show()
                            }
                            if(viewModel.authViewModelState == false){
                                Snackbar.make(view,"Failed",Snackbar.LENGTH_SHORT).show()
                            }
                            //animasyonu durdurup view'i visible yapıyoruz
                            enableViewDisableLottie(lottieAnimationView,view)
                            if (findNavController != null && action != null){
                                findNavController.navigate(action,null, navOptions )
                            }
                        }
                    }
                }

                if(viewModel is UsersBirthdayViewModel){  //gelen viewModel Birthday viewmodel ise bu çalışacak
                           Handler(Looper.getMainLooper()).postDelayed({
                               //animasyonu durdurup view'i visible yapıyoruz
                               enableViewDisableLottie(lottieAnimationView,view)
                               if (findNavController != null && action != null){
                                   findNavController.navigate(action,null,navOptions)
                               }
                           },1500)
                }
            }
        }
    }


    fun drawerLayoutToggle(drawerLayout: DrawerLayout,
                           navigationView: NavigationView,
                           findNavController: NavController,
                           menuButtonToolbar:View,
                           activity: Activity,
                           authViewModel: AuthViewModel,
                           birthdayRepository:BirthdayRepository,
                           userSharedPreferences:UserSharedPreferencesManager,
                           sourcePage:String
    ){

        // ActionBarDrawerToggle ile Drawer'ı ActionBar ile senkronize etme
        val toggle = ActionBarDrawerToggle(
            activity,
            drawerLayout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // NavigationView'deki öğeler için click listener
        navigationView.setNavigationItemSelectedListener { menuItem ->
            // Menü öğelerine tıklandığında yapılacak işlemler
            when (menuItem.itemId) {
                R.id.labelBirthdays -> {
                    when(sourcePage){
                        "TrashBin"-> findNavController.navigate(R.id.trashToMainPage)
                        "Settings"-> findNavController.navigate(R.id.settingsToMainPage)
                        "Profile"-> findNavController.navigate(R.id.profileToMain)
                        "DeletedBirthdayDetail"-> findNavController.navigate(R.id.deletedDetailToMain)
                        "BirthdayEdit"-> findNavController.navigate(R.id.editToMain)
                        "BirthdayDetail"-> findNavController.navigate(R.id.birthdayDetailToMain)
                        "AddBirthday"-> findNavController.navigate(R.id.addToMain)
                        "PastBirthdays"-> findNavController.navigate(R.id.addToPastBirthdays)
                    }
                }

                R.id.labelLogOut -> {
                    authViewModel.logoutUser()
                    userSharedPreferences.clearUserSession()
                    birthdayRepository.clearBirthdays()
                    birthdayRepository.clearDeletedBirthdays()
                    birthdayRepository.clearPastBirthdays()
                    logout(activity)

                }

                R.id.labelTrashBin -> {
                    when(sourcePage){
                        "MainPage"-> findNavController.navigate(R.id.mainToTrashBin)
                        "TrashBin"-> findNavController.navigate(R.id.trashToTrashBin)
                        "Settings"-> findNavController.navigate(R.id.settingsToTrashBin)
                        "Profile"-> findNavController.navigate(R.id.profileToTrash)
                        "DeletedBirthdayDetail"-> findNavController.navigate(R.id.deletedDetailToTrashBin)
                        "BirthdayEdit"-> findNavController.navigate(R.id.editToTrash)
                        "BirthdayDetail"-> findNavController.navigate(R.id.detailToTrash)
                        "AddBirthday"-> findNavController.navigate(R.id.addToTrash)
                        "PastBirthdays"-> findNavController.navigate(R.id.pastBirthdaysToTrashBin)
                    }
                }

                R.id.labelSettings -> {
                    when(sourcePage){
                        "MainPage"-> findNavController.navigate(R.id.mainToSettings)
                        "TrashBin"-> findNavController.navigate(R.id.trashToSettings)
                        "Profile"-> findNavController.navigate(R.id.profileToSettings)
                        "DeletedBirthdayDetail"-> findNavController.navigate(R.id.deletedDetailToSettings)
                        "BirthdayEdit"-> findNavController.navigate(R.id.editToSettings)
                        "BirthdayDetail"-> findNavController.navigate(R.id.detailToSettings)
                        "AddBirthday"-> findNavController.navigate(R.id.addToSettings)
                        "PastBirthdays"-> findNavController.navigate(R.id.pastBirthdaysToSettings)
                    }
                }

                R.id.labelProfile -> {
                    when(sourcePage){
                        "MainPage"-> findNavController.navigate(R.id.mainToProfile)
                        "TrashBin"-> findNavController.navigate(R.id.trashToProfile)
                        "DeletedBirthdayDetail"-> findNavController.navigate(R.id.deletedDetailToProfile)
                        "BirthdayEdit"-> findNavController.navigate(R.id.editToProfile)
                        "BirthdayDetail"-> findNavController.navigate(R.id.detailToProfile)
                        "AddBirthday"-> findNavController.navigate(R.id.addToProfile)
                        "PastBirthdays"-> findNavController.navigate(R.id.pastBirthdaysToProfile)
                    }
                }
                R.id.labelPastBirthdays -> {
                    when(sourcePage){
                        "MainPage"-> findNavController.navigate(R.id.mainToPastBirthdays)
                        "TrashBin"-> findNavController.navigate(R.id.trashToPastBirthdays)
                        "DeletedBirthdayDetail"-> findNavController.navigate(R.id.deletedDetailToPastBirthdays)
                        "BirthdayEdit"-> findNavController.navigate(R.id.editToPastBirthdays)
                        "BirthdayDetail"-> findNavController.navigate(R.id.detailToPastBirthdays)
                        "AddBirthday"-> findNavController.navigate(R.id.addToPastBirthdays)
                        "PastBirthdays"-> findNavController.navigate(R.id.pastBirthdaysToPastBirthdays)
                    }
                }

                else -> false
            }

            // Drawer'ı kapatmak için
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }


        // Toolbar üzerindeki menü ikonu ile menüyü açma
       menuButtonToolbar.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }


    }

     fun navigateToFragmentAndClearStack(navController: NavController, currentFragmentId: Int, targetFragmentId: Int) {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(currentFragmentId, inclusive = true)
            .build()

        navController.navigate(targetFragmentId, null, navOptions)
    }

    // Logout fonksiyonu
    private fun logout(activity: Activity) {
        // Burada oturum kapatma işlemlerini gerçekleştirin

        // Mevcut aktiviteyi kapat ve yeni bir aktivite başlat
        val intent = Intent(activity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        activity.startActivity(intent)
        activity.finish()
    }


}