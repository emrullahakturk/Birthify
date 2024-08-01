
@file:Suppress("UNUSED_EXPRESSION")

package com.yargisoft.birthify

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
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
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.yargisoft.birthify.adapters.BirthdayAdapter
import com.yargisoft.birthify.adapters.DeletedBirthdayAdapter
import com.yargisoft.birthify.models.Birthday
import com.yargisoft.birthify.repositories.BirthdayRepository
import com.yargisoft.birthify.repositories.GuestRepository
import com.yargisoft.birthify.sharedpreferences.UserSharedPreferencesManager
import com.yargisoft.birthify.viewmodels.AuthViewModel
import com.yargisoft.birthify.viewmodels.GuestBirthdayViewModel
import com.yargisoft.birthify.viewmodels.UsersBirthdayViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


object GuestFrequentlyUsedFunctions {


    //EditText ile arama yaparken aramayı filtrelemek için kullanılan fonksiyon
    fun filterBirthdays(query: String, guestBirthdayViewModel: GuestBirthdayViewModel, adapter: DeletedBirthdayAdapter) {
        val birthdays = guestBirthdayViewModel.deletedBirthdayList.value
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
    fun filterBirthdays(query: String, guestBirthdayViewModel:GuestBirthdayViewModel, adapter: BirthdayAdapter) {
        val birthdays = guestBirthdayViewModel.birthdayList.value
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
                     guestBirthdayViewModel:GuestBirthdayViewModel){

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
            handleSortOptionSelected(menuItem,adapter,guestBirthdayViewModel)
            true
        }
        popupMenu.show()

    }
    private fun handleSortOptionSelected(menuItem: MenuItem,
                                         adapter: BirthdayAdapter,
                                         guestBirthdayViewModel:GuestBirthdayViewModel) {

        val sortedList = when (menuItem.itemId) {
            R.id.sort_by_name_asc -> guestBirthdayViewModel.sortBirthdaysMainPage("sortBirthdaysByNameAsc")
            R.id.sort_by_birth_date_asc -> guestBirthdayViewModel.sortBirthdaysMainPage("sortBirthdaysByBirthdayDateAsc")
            R.id.sort_by_recorded_date_asc -> guestBirthdayViewModel.sortBirthdaysMainPage("sortBirthdaysByRecordedDateAsc")
            R.id.sort_by_name_dsc -> guestBirthdayViewModel.sortBirthdaysMainPage("sortBirthdaysByNameDsc")
            R.id.sort_by_birth_date_dsc -> guestBirthdayViewModel.sortBirthdaysMainPage("sortBirthdaysByBirthdayDateDsc")
            R.id.sort_by_recorded_date_dsc -> guestBirthdayViewModel.sortBirthdaysMainPage("sortBirthdaysByRecordedDateDsc")
            else -> emptyList()
        }

        adapter.updateData(sortedList)
    }
    //Main Page için sort menüsünü açma fonksiyonları


    // Deleted (Trash Bin) Page için sort menüsünü açma fonksiyonları
    fun showSortMenu(view: View, context:Context, adapter: DeletedBirthdayAdapter, guestBirthdayViewModel:GuestBirthdayViewModel) {
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
            handleSortOptionSelected(menuItem,adapter,guestBirthdayViewModel)
            true
        }
        popupMenu.show()
    }
    private fun handleSortOptionSelected(menuItem: MenuItem, adapter: DeletedBirthdayAdapter, guestBirthdayViewModel:GuestBirthdayViewModel) {
        val sortedList = when (menuItem.itemId) {
            R.id.sort_by_name_asc -> guestBirthdayViewModel.sortBirthdaysTrashBin("sortBirthdaysByNameAsc")
            R.id.sort_by_birth_date_asc -> guestBirthdayViewModel.sortBirthdaysTrashBin("sortBirthdaysByBirthdayDateAsc")
            R.id.sort_by_recorded_date_asc -> guestBirthdayViewModel.sortBirthdaysTrashBin("sortBirthdaysByRecordedDateAsc")
            R.id.sort_by_name_dsc -> guestBirthdayViewModel.sortBirthdaysTrashBin("sortBirthdaysByNameDsc")
            R.id.sort_by_birth_date_dsc -> guestBirthdayViewModel.sortBirthdaysTrashBin("sortBirthdaysByBirthdayDateDsc")
            R.id.sort_by_recorded_date_dsc -> guestBirthdayViewModel.sortBirthdaysTrashBin("sortBirthdaysByRecordedDateDsc")
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
    fun showConfirmationDialog(view: View, context: Context, guestBirthdayViewModel:GuestBirthdayViewModel, editedBirthday:Birthday, lottieAnimationView: LottieAnimationView, viewLifecycleOwner:LifecycleOwner, condition: String, findNavController: NavController, action:Int )
    {
        AlertDialog.Builder(context)
            .setTitle("Confirm Operation")
            .setMessage("Are you sure you want to do this operation?")
            .setPositiveButton("Yes") { _, _ ->

                when (condition) {
                    "permanently" -> {
                        guestBirthdayViewModel.permanentlyDeleteBirthday(editedBirthday.id)
                    }
                    "soft_delete" -> {
                        guestBirthdayViewModel.deleteBirthday(editedBirthday.id)
                    }
                    "re_save" -> {
                        guestBirthdayViewModel.reSaveDeletedBirthday(editedBirthday.id)
                    }
                }

                loadAndStateOperation(viewLifecycleOwner,guestBirthdayViewModel,lottieAnimationView,view,findNavController,action)
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
                            Log.e("tagımıs","is loaded işlemi")

                            if(authViewModel.authViewModelState == true){
                                if (authViewModel.isEmailVerifiedResult == true) {
                                    userSharedPreferences.saveIsChecked(isChecked)
                                    Snackbar.make(view, "You successfully logged in", Snackbar.LENGTH_SHORT).show()
                                    Log.e("tagımıs","navigate işlemi")
                                    findNavController.navigate(action)

                                } else {
                                    userSharedPreferences.apply {
                                        clearUserSession()
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
                                   action: Int
    ){

        if(isValidPassword(password) && isValidEmail(email) && isValidFullName(name) )
        {

            //user kaydetme fonksiyonunu viewmodeldan çağırıyoruz
            viewModel.registerUser(name,email,password)

            disableViewEnableLottie(lottieAnimationView,view)

            isLoadingCheck(viewLifecycleOwner,viewModel,lottieAnimationView,view,findNavController,action)


        }else{
            Snackbar.make(view,"Please correctly fill in all fields",Snackbar.LENGTH_SHORT).show()
        }
    }



    //Swipe ederek silme işlemi yaparken UserSwipeToDeleteCallback sınıfından bu fonksiyon çağrılır ve silme işlemi başlatılır
    @SuppressLint("NotifyDataSetChanged")
    fun showDeleteDialogBirthdayAdapter(position: Int, view: View, context: Context, birthdayList:List<Birthday>, lottieAnimationView: LottieAnimationView, guestBirthdayViewModel:GuestBirthdayViewModel, lifeCycleOwner: LifecycleOwner, findNavController: NavController, action: Int
    ){
        Log.e("HATA","$birthdayList")

        if (birthdayList.isNotEmpty()){
            val birthday = birthdayList[position]
            showConfirmationDialog(view,context,guestBirthdayViewModel,birthday,lottieAnimationView,lifeCycleOwner,"soft_delete", findNavController , action )
        }
    }




    /*Şifre sıfırlama ekranında (Forgot Password Page) kutucuğa uazılan mailin validayion işlemlerini,
     animasyon işlemlerini vs yapan fonksiyon
     */
    fun resetPasswordValidation(email:String, viewLifecycleOwner:LifecycleOwner, viewModel:AuthViewModel, lottieAnimationView: LottieAnimationView, view: View, findNavController: NavController, action:Int )
    {
        if(isValidEmail(email)){
            viewModel.resetPassword(email)
            disableViewEnableLottie(lottieAnimationView,view)
            isLoadingCheck( viewLifecycleOwner, viewModel, lottieAnimationView, view, findNavController, action)
        }

    }



    //Yaptığımız suspend işlemlerin tamamlanıp tamamlanmadığını kontrol eden fonksiyon
    // aynı zamanda arayüzü kilitleyip kullanıcının ekranda işlemler yapmasını engelliyor (disableViewEnableLottie ile)
    //isloading işlemin tamamlanıp tamamlanmadığını dönderiyor ve içerisindeki enableViewDisableLottie fonksiyonu ile arayüzü
    //kullanıcının kullanımına açıyor
    fun loadAndStateOperation(viewLifecycleOwner:LifecycleOwner, guestBirthdayViewModel:GuestBirthdayViewModel, lottieAnimationView: LottieAnimationView, view:View, findNavController: NavController, action:Int )
    {
        disableViewEnableLottie(lottieAnimationView,view)
        isLoadingCheck(viewLifecycleOwner,guestBirthdayViewModel,lottieAnimationView,view, findNavController, action)
    }


    //Auth View Model ve UsersBirthdayViewModel için isLoading Kontrolü yapan fonksiyon
    // (Suspend fonksiyonun bitip bitmediğini kontrol ediyoruz)
    private fun isLoadingCheck(viewLifecycleOwner: LifecycleOwner, viewModel:ViewModel, lottieAnimationView: LottieAnimationView, view: View, findNavController: NavController?, action: Int?)
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
                                findNavController.navigate(action)
                            }
                        }
                    }
                }

                if(viewModel is UsersBirthdayViewModel){  //gelen viewModel Birthday viewmodel ise bu çalışacak
                    Handler(Looper.getMainLooper()).postDelayed({
                        //animasyonu durdurup view'i visible yapıyoruz
                        enableViewDisableLottie(lottieAnimationView,view)
                        if (findNavController != null && action != null){
                            findNavController.navigate(action)
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
                                birthdayRepository:GuestRepository,
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
                        "GuestMainPage"-> findNavController.navigate(R.id.mainToMain)
                        "GuestTrashBin"-> findNavController.navigate(R.id.trashToMainPage)
                        "GuestSettings"-> findNavController.navigate(R.id.settingsToMainPage)
                        "GuestProfile"-> findNavController.navigate(R.id.profileToMain)
                        "GuestDeletedBirthdayDetail"-> findNavController.navigate(R.id.deletedDetailToMain)
                        "GuestBirthdayEdit"-> findNavController.navigate(R.id.editToMain)
                        "GuestBirthdayDetail"-> findNavController.navigate(R.id.birthdayDetailToMain)
                        "GuestAddBirthday"-> findNavController.navigate(R.id.addToMain)
                    }
                }

                R.id.labelLogOut -> {
                    userSharedPreferences.clearUserSession()
                    birthdayRepository.clearBirthdays()
                    birthdayRepository.clearDeletedBirthdays()
                    findNavController.navigate(R.id.firstPageFragment)
                }

                R.id.labelTrashBin -> {
                    when(sourcePage){
                        "GuestMainPage"-> findNavController.navigate(R.id.mainToTrashBin)
                        "GuestTrashBin"-> findNavController.navigate(R.id.trashToTrashBin)
                        "GuestSettings"-> findNavController.navigate(R.id.settingsToTrashBin)
                        "GuestProfile"-> findNavController.navigate(R.id.profileToTrash)
                        "GuestDeletedBirthdayDetail"-> findNavController.navigate(R.id.deletedDetailToTrashBin)
                        "GuestBirthdayEdit"-> findNavController.navigate(R.id.editToTrash)
                        "GuestBirthdayDetail"-> findNavController.navigate(R.id.detailToTrash)
                        "GuestAddBirthday"-> findNavController.navigate(R.id.addToTrash)
                    }
                }

                R.id.labelSettings -> {
                    when(sourcePage){
                        "GuestMainPage"-> findNavController.navigate(R.id.mainToSettings)
                        "GuestTrashBin"-> findNavController.navigate(R.id.trashToSettings)
                        "GuestProfile"-> findNavController.navigate(R.id.profileToSettings)
                        "GuestDeletedBirthdayDetail"-> findNavController.navigate(R.id.deletedDetailToSettings)
                        "GuestBirthdayEdit"-> findNavController.navigate(R.id.editToSettings)
                        "GuestBirthdayDetail"-> findNavController.navigate(R.id.detailToSettings)
                        "GuestAddBirthday"-> findNavController.navigate(R.id.addToSettings)
                    }
                }

                R.id.labelProfile -> {
                    when(sourcePage){
                        "GuestMainPage"-> findNavController.navigate(R.id.mainToProfile)
                        "GuestTrashBin"-> findNavController.navigate(R.id.trashToProfile)
                        "GuestDeletedBirthdayDetail"-> findNavController.navigate(R.id.deletedDetailToProfile)
                        "GuestBirthdayEdit"-> findNavController.navigate(R.id.editToProfile)
                        "GuestBirthdayDetail"-> findNavController.navigate(R.id.detailToProfile)
                        "GuestAddBirthday"-> findNavController.navigate(R.id.addToProfile)
                    }                  }

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


}