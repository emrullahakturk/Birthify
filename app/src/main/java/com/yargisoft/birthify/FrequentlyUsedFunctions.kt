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
import android.widget.CheckBox
import android.widget.PopupMenu
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.yargisoft.birthify.adapters.BirthdayAdapter
import com.yargisoft.birthify.adapters.DeletedBirthdayAdapter
import com.yargisoft.birthify.models.Birthday
import com.yargisoft.birthify.sharedpreferences.UserSharedPreferencesManager
import com.yargisoft.birthify.viewmodels.AuthViewModel
import com.yargisoft.birthify.viewmodels.BirthdayViewModel
import com.yargisoft.birthify.views.auth_views.RegisterFragmentDirections
import kotlinx.coroutines.launch
import java.util.Calendar


object FrequentlyUsedFunctions {


    //EditText ile arama yaparken aramayı filtrelemek için kullanılan fonksiyon
     fun filterBirthdays(query: String,viewModel:BirthdayViewModel,adapter: DeletedBirthdayAdapter) {
        val birthdays = viewModel.birthdays.value
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
     fun filterBirthdays(query: String,viewModel:BirthdayViewModel,adapter: BirthdayAdapter) {
        val birthdays = viewModel.birthdays.value
        if (birthdays != null) {
            val filteredBirthdays = if (query.isEmpty()) {
                birthdays
            } else {
                birthdays.filter { it.name.contains(query, ignoreCase = true) }
            }
            adapter.updateData(filteredBirthdays)
        }
    }



    //Main Page için sort menüsünü açma fonksiyonları
        fun showSortMenu(view: View,context:Context, adapter: BirthdayAdapter, birthdayViewModel: BirthdayViewModel) {
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
            handleSortOptionSelected(menuItem,adapter,birthdayViewModel)
            true
        }
        popupMenu.show()
    }
        private fun handleSortOptionSelected(menuItem: MenuItem, adapter: BirthdayAdapter, birthdayViewModel: BirthdayViewModel) {
        val sortedList = when (menuItem.itemId) {
            R.id.sort_by_name_asc -> birthdayViewModel.sortBirthdaysByNameAsc()
            R.id.sort_by_birth_date_asc -> birthdayViewModel.sortBirthdaysByBirthdayDateAsc()
            R.id.sort_by_recorded_date_asc -> birthdayViewModel.sortBirthdaysByRecordedDateAsc()
            R.id.sort_by_name_dsc -> birthdayViewModel.sortBirthdaysByNameDsc()
            R.id.sort_by_birth_date_dsc -> birthdayViewModel.sortBirthdaysByBirthdayDateDsc()
            R.id.sort_by_recorded_date_dsc -> birthdayViewModel.sortBirthdaysByRecordedDateDsc()
            else -> emptyList()
        }

        adapter.updateData(sortedList)
    }
    //Main Page için sort menüsünü açma fonksiyonları



    // Deleted (Trash Bin) Page için sort menüsünü açma fonksiyonları
        fun showSortMenu(view: View,context:Context, adapter: DeletedBirthdayAdapter, birthdayViewModel: BirthdayViewModel) {
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
            handleSortOptionSelected(menuItem,adapter,birthdayViewModel)
            true
        }
        popupMenu.show()
    }
        private fun handleSortOptionSelected(menuItem: MenuItem, adapter: DeletedBirthdayAdapter, birthdayViewModel: BirthdayViewModel) {
        val sortedList = when (menuItem.itemId) {
            R.id.sort_by_name_asc -> birthdayViewModel.sortBirthdaysByNameAsc()
            R.id.sort_by_birth_date_asc -> birthdayViewModel.sortBirthdaysByBirthdayDateAsc()
            R.id.sort_by_recorded_date_asc -> birthdayViewModel.sortBirthdaysByRecordedDateAsc()
            R.id.sort_by_name_dsc -> birthdayViewModel.sortBirthdaysByNameDsc()
            R.id.sort_by_birth_date_dsc -> birthdayViewModel.sortBirthdaysByBirthdayDateDsc()
            R.id.sort_by_recorded_date_dsc -> birthdayViewModel.sortBirthdaysByRecordedDateDsc()
            else -> emptyList()
        }

        adapter.updateData(sortedList)
    }
    //Deleted (Trash Bin) Page için sort menüsünü açma fonksiyonları




    // Tarih seçme ekranı için gereken fonksiyon
    fun showDatePickerDialog(context:Context, editText: TextInputEditText ) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(context, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = "$selectedYear-${String.format("%02d", selectedMonth + 1)}-${String.format("%02d", selectedDay)}"
            editText.setText(selectedDate)
        }, year, month, day)

        datePickerDialog.show()
    }



    //parametre olarak gelen view'i devre dışı bırakmak ve tekrar aktif etmek için kullanılan fonksiyon
     fun setViewAndChildrenEnabled(view: View, enabled: Boolean) {
        view.isEnabled = enabled
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                setViewAndChildrenEnabled(view.getChildAt(i), enabled)
            }
        }
    }

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



    //Doğum gününü kalıcı olarak silmek için onay diyaloğu çıkaran fonksiyon
    fun showPermanentlyDeleteConfirmationDialog(
        view: View, context: Context,
        birthdayViewModel: BirthdayViewModel,
        lottieAnimationView: LottieAnimationView,
        viewLifecycleOwner: LifecycleOwner,
        birthday: Birthday,
        findNavController: NavController
    ) {

        AlertDialog.Builder(context)
            .setTitle("Confirm Deleting Permanently")
            .setMessage("Are you sure you want to permanently delete this birthday?")
            .setPositiveButton("Yes") { _, _ ->

                birthdayViewModel.deleteBirthdayPermanently(birthday.id)

                lottieAnimationView.playAnimation()
                lottieAnimationView.visibility = View.VISIBLE
                setViewAndChildrenEnabled(view,false)

                Handler(Looper.getMainLooper()).postDelayed({
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            birthdayViewModel.isLoaded.collect { isLoaded ->
                                if(isLoaded ==true){
                                    //animasyonu durdurup view'i visible yapıyoruz
                                    lottieAnimationView.cancelAnimation()
                                    lottieAnimationView.visibility = View.INVISIBLE
                                    setViewAndChildrenEnabled(view,true)
                                }
                            }
                        }
                    }

                    viewLifecycleOwner.lifecycleScope.launch {
                        viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                            birthdayViewModel.permanentlyDeleteBirthdayState.collect{ isPermanentlyDeleted->
                                if (isPermanentlyDeleted == true){
                                    Snackbar.make(view,"Birthday permanently deleted successfully", Snackbar.LENGTH_SHORT).show()
                                    findNavController.navigateUp()
                                }
                                if (isPermanentlyDeleted == false){

                                    Snackbar.make(view,"Failed to permanently delete birthday",
                                        Snackbar.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }


                },3000)

            }
            .setNegativeButton("No"){_,_->
                //animasyonu durdurup view'i visible yapıyoruz
                lottieAnimationView.cancelAnimation()
                lottieAnimationView.visibility = View.INVISIBLE
                setViewAndChildrenEnabled(view,true)
            }
            .show()
    }


    //Doğum gününü geri kaydetmek için onay diyaloğu çıkaran fonksiyon
     fun showReSaveConfirmationDialog(view:View,
                                      context: Context,
                                      birthdayViewModel: BirthdayViewModel,
                                      lottieAnimationView: LottieAnimationView,
                                      viewLifecycleOwner: LifecycleOwner,
                                      birthday: Birthday,
                                      findNavController: NavController) {
        AlertDialog.Builder(context)
            .setTitle("Confirm Re Saving")
            .setMessage("Are you sure you want to re save this birthday?")
            .setPositiveButton("Yes") { _, _ ->

                birthdayViewModel.reSaveDeletedBirthday(birthday.id, birthday)

                 lottieAnimationView.playAnimation()
                 lottieAnimationView.visibility = View.VISIBLE
                 setViewAndChildrenEnabled(view,false)

                Handler(Looper.getMainLooper()).postDelayed({
                    viewLifecycleOwner.lifecycleScope.launch {
                        viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            birthdayViewModel.isLoaded.collect { isLoaded ->
                                if(isLoaded == true){
                                    //animasyonu durdurup view'i visible yapıyoruz
                                   lottieAnimationView.cancelAnimation()
                                    lottieAnimationView.visibility = View.INVISIBLE
                                    FrequentlyUsedFunctions.setViewAndChildrenEnabled(view,true)
                                }
                            }
                        }
                    }

                    viewLifecycleOwner.lifecycleScope.launch {
                        viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                            birthdayViewModel.reSaveDeletedBirthdayState.collect{ isResaved->
                                if (isResaved == true){
                                    Snackbar.make(view,"Birthday re saved successfully", Snackbar.LENGTH_SHORT).show()
                                    findNavController.navigateUp()
                                }
                                if (isResaved == false){
                                    Snackbar.make(view,"Failed to re save birthday",Snackbar.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                },3000)
            }
            .setNegativeButton("No"){_,_->
                //animasyonu durdurup view'i visible yapıyoruz
                lottieAnimationView.cancelAnimation()
                lottieAnimationView.visibility = View.INVISIBLE
                setViewAndChildrenEnabled(view,true)
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
                                findNavController: NavController
                                ){
        if (isValidEmail(email) && password.isNotEmpty()) {
            authViewModel.loginUser(email, password,isChecked)

            lottieAnimationView.visibility = View.VISIBLE
            lottieAnimationView.playAnimation()
            setViewAndChildrenEnabled(view,false)


            Handler(Looper.getMainLooper()).postDelayed({
                viewLifecycleOwner.lifecycleScope.launch {
                    viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        authViewModel.isLoading.collect { isLoading ->

                            if(!isLoading){
                                //animasyonu durdurup view'i visible yapıyoruz
                                lottieAnimationView.cancelAnimation()
                                lottieAnimationView.visibility = View.INVISIBLE
                                setViewAndChildrenEnabled(view,true)
                            }
                        }
                    }
                }
                viewLifecycleOwner.lifecycleScope.launch {
                    viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        authViewModel.loginResult.collect { result ->
                            if (result == true) {
                                if (authViewModel.isEmailVerifiedResult != true){
                                    userSharedPreferences.clearUserSession()
                                    Snackbar.make(view,"Please verify your e-mail",Snackbar.LENGTH_SHORT).show()
                                }
                                else{
                                    if(isChecked){
                                        userSharedPreferences.saveIsChecked(true )
                                    }else{
                                        userSharedPreferences.saveIsChecked(false )
                                    }

                                    Snackbar.make(view,"You successfully logged in",Snackbar.LENGTH_SHORT).show()
                                    findNavController.navigate(R.id.loginToMain)
                                }
                            }else{
                                Snackbar.make(view,"Login failed",Snackbar.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }, 5000) // 2 saniye bekletme

        }else {
            Snackbar.make(view,"Please fill in all fields",Snackbar.LENGTH_SHORT).show()
        }
    }


    fun registerValidationFunction(email:String,password: String,name:String,
                                   viewModel:AuthViewModel,
                                   lottieAnimationView: LottieAnimationView,
                                   viewLifecycleOwner:LifecycleOwner,
                                   view:View,
                                   findNavController:NavController,
                                   topLayout: ConstraintLayout){
        if(
            isValidPassword(password)
            && isValidEmail(email)
            && isValidFullName(name)
        ){

            //user kaydetme fonksiyonunu viewmodeldan çağırıyoruz
            viewModel.registerUser(name,email,password)

            topLayout.visibility = View.INVISIBLE
            lottieAnimationView.visibility = View.VISIBLE
            lottieAnimationView.playAnimation()

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.isLoading.collect { isLoading ->
//                            Log.e("tagımıs", " yüklenme durumu fragment: $isLoading")

                        if(!isLoading){
                            //animasyonu durdurup view'i visible yapıyoruz
                            lottieAnimationView.cancelAnimation()
                            lottieAnimationView.visibility = View.INVISIBLE
                            topLayout.visibility = View.VISIBLE

                        }
                    }
                }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.registrationResult.collect { result ->
                        Log.e("tagımıs", " kayıt sonucu: $result")

                        if (result == true) {
                            // Kayıt başarılı
                            Snackbar.make(view,"Registration successfully, please verify your email",Snackbar.LENGTH_SHORT).show()
                            val action = RegisterFragmentDirections.registerToLogin(email,password,"Register")
                            findNavController.navigate(action)
                        } else if (result == false) {
                            // Kayıt başarısız
                            Snackbar.make(view,"Registration failed",Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
            }

        }else{
            Snackbar.make(view,"Please correctly fill in all fields",Snackbar.LENGTH_SHORT).show()
        }
    }



//Swipe ederek silme işlemi yaparken SwipeToDeleteCallback sınıfından bu fonksiyon çağrılır ve silme işlemi başlatılır
    @SuppressLint("NotifyDataSetChanged")
    fun showDeleteDialogBirthdayAdapter(position: Int,
                                        view: View,
                                        context: Context,
                                        birthdayList:List<Birthday>,
                                        lottieAnimationViewDelete: LottieAnimationView,
                                        lottieAnimationViewThreePoint: LottieAnimationView,
                                        birthdayViewModel: BirthdayViewModel,
                                        lifeCycleOwner: LifecycleOwner,
                                        userPreferences:UserSharedPreferencesManager
    ){
      //  val view = (context as Activity).findViewById<View>(android.R.id.content)
        if (birthdayList.isNotEmpty()){
            val birthday = birthdayList[position]
            AlertDialog.Builder(context)
                .setTitle("Delete Birthday")
                .setMessage("Are you sure you want to delete this birthday?")
                .setPositiveButton("Yes") { _, _ ->

                    lottieAnimationViewDelete.visibility = View.VISIBLE
                    lottieAnimationViewDelete.playAnimation()
                    lottieAnimationViewThreePoint.visibility = View.VISIBLE
                    lottieAnimationViewThreePoint.playAnimation()
                    setViewAndChildrenEnabled(view, false)

                    // Silme işlemini yap
                    birthdayViewModel.deleteBirthday(birthday.id,birthday)

//                        Handler(Looper.getMainLooper()).postDelayed({
                    lifeCycleOwner.lifecycleScope.launch {
                        lifeCycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                            birthdayViewModel.deleteBirthdayState.collect{isDeleted ->
                                if (isDeleted == true){ birthdayViewModel.getUserBirthdays(userPreferences.getUserId())
                                    Snackbar.make(view, "Birthday successfully deleted", Snackbar.LENGTH_LONG).show()
                                }
                                if (isDeleted == false){
                                    Snackbar.make(view, "Birthday deleting failed", Snackbar.LENGTH_LONG).show()
                                }

                            }
                        }
                    }
                    lifeCycleOwner.lifecycleScope.launch{
                        lifeCycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                            birthdayViewModel.isLoaded.collect{ isLoaded ->
                                if(isLoaded == true){
                                    setViewAndChildrenEnabled(view, true)
                                    lottieAnimationViewDelete.visibility = View.INVISIBLE
                                    lottieAnimationViewDelete.cancelAnimation()
                                    lottieAnimationViewThreePoint.visibility = View.INVISIBLE
                                    lottieAnimationViewThreePoint.cancelAnimation()
                                }
                            }
                        }
                    }
//                        },2000)
                }
                .setNegativeButton("No") { _, _ ->
                    // Eğer silme iptal edilirse öğeyi geri yerleştir
                    birthdayViewModel.getUserBirthdays(userPreferences.getUserId())
                }
                .show()
        }
    }




    /*Şifre sıfırlama ekranında (Forgot Password Page) kutucuğa uazılan mailin validayion işlemlerini,
     animasyon işlemlerini vs yapan fonksiyon
     */
    fun resetPasswordValidation(
        viewLifecycleOwner:LifecycleOwner,
        viewModel:AuthViewModel,
        lottieAnimationView: LottieAnimationView,
        view: View,
        findNavController: NavController,
        topLayout: ConstraintLayout

                                ){
        Handler(Looper.getMainLooper()).postDelayed({
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                    viewModel.isResetMailSent.collect { isSent ->
                        if(isSent==true){
                            //animasyonu durdurup view'i visible yapıyoruz
                            lottieAnimationView.cancelAnimation()
                            lottieAnimationView.visibility = View.INVISIBLE
                            topLayout.visibility = View.VISIBLE
                            Snackbar.make(view,"Password reset e-mail sent",Snackbar.LENGTH_SHORT).show()
                            Handler(Looper.getMainLooper()).postDelayed({
                                findNavController.navigate(R.id.forgotToLogin)
                            },1000)

                        }else{
                            //animasyonu durdurup view'i visible yapıyoruz
                            lottieAnimationView.cancelAnimation()
                            lottieAnimationView.visibility = View.INVISIBLE
                            topLayout.visibility = View.VISIBLE
                            Snackbar.make(view,"Sending e-mail failed",Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        },3000)
    }




    /* Doğum günü ekleme sayfasında (Add Birthday Page) doğum günü ekleme validasyonlarını,
        animasyon kontrollerini, UI işlemlerini yapan fonksiyon
       */
    fun addBirthdayValidation(
        viewLifecycleOwner:LifecycleOwner,
        birthdayViewModel: BirthdayViewModel,
        lottieAnimationView: LottieAnimationView,
        view:View,
        findNavController: NavController,
        topLayout: ConstraintLayout,
        constraintLayout:ConstraintLayout
                              ){

        Handler(Looper.getMainLooper()).postDelayed({
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    birthdayViewModel.isLoaded.collect { isLoaded ->
                        if (isLoaded==true) {
                            //animasyonu durdurup view'i visible yapıyoruz
                            lottieAnimationView.cancelAnimation()
                            lottieAnimationView.visibility = View.INVISIBLE
                            constraintLayout.setBackgroundResource(0)
                            topLayout.visibility = View.VISIBLE
                        }

                    }
                }
            }
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    birthdayViewModel.saveBirthdayState.collect { isSaved ->
                        if(isSaved ==true){
                            Snackbar.make(view,"Birthday saved successfully",Snackbar.LENGTH_SHORT).show()
                            findNavController.popBackStack()
                        }
                        if(isSaved == false){
                            Snackbar.make(view,"Failed to save birthday",Snackbar.LENGTH_SHORT).show()
                        }

                    }
                }
            }
        },2000)
    }



    fun updateBirthdayEditPage(
        view: View,
        lottieAnimationView: LottieAnimationView,
        viewLifecycleOwner:LifecycleOwner,
        viewModel:BirthdayViewModel,
        findNavController: NavController,
                               ){

        setViewAndChildrenEnabled(view, false)
        lottieAnimationView.visibility = View.VISIBLE
        lottieAnimationView.playAnimation()

        Handler(Looper.getMainLooper()).postDelayed({
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                    viewModel.updateBirthdayState.collect{isUpdated->
                        if(isUpdated ==true){
                            Snackbar.make(view,"Birthday updated successfully",
                                Snackbar.LENGTH_SHORT
                            ).show()
                            findNavController.popBackStack()
                        }
                        if (isUpdated == false){
                            Snackbar.make(view,"Failed to update the birthday",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                setViewAndChildrenEnabled(view, true)
                lottieAnimationView.cancelAnimation()
                lottieAnimationView.visibility = View.INVISIBLE

            }
        },3000)
    }


     fun showDeleteConfirmationDialog(view: View,
                                      context: Context,
                                      viewModel:BirthdayViewModel,
                                      editedBirthday:Birthday,
                                      lottieAnimationView: LottieAnimationView,
                                      viewLifecycleOwner:LifecycleOwner,
                                      findNavController: NavController,
                                      ){

        AlertDialog.Builder(context)
            .setTitle("Confirm Deletion")
            .setMessage("Are you sure you want to delete this birthday?")
            .setPositiveButton("Yes") { _, _ ->

                viewModel.deleteBirthday(editedBirthday.id, editedBirthday)

                FrequentlyUsedFunctions.setViewAndChildrenEnabled(view, false)
                lottieAnimationView.playAnimation()
                lottieAnimationView.visibility = View.VISIBLE

                Handler(Looper.getMainLooper()).postDelayed({
                    viewLifecycleOwner.lifecycleScope.launch{
                        viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED){
                            viewModel.deleteBirthdayState.collect { isDeleted ->
                                if(isDeleted == true){
                                    findNavController.navigateUp()
                                    Snackbar.make(view,"Birthday deleted successfully",
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                }
                                if (isDeleted == false){
                                    Snackbar.make(view,"Failed to delete birthday",
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                }

                            }
                        }
                        FrequentlyUsedFunctions.setViewAndChildrenEnabled(view, true)
                        lottieAnimationView.cancelAnimation()
                        lottieAnimationView.visibility = View.INVISIBLE
                    }
                },3000)

            }
            .setNegativeButton("No",null)
            .show()
    }

}


