package com.yargisoft.birthify.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yargisoft.birthify.repositories.BirthdayRepository
import com.yargisoft.birthify.viewmodels.UsersBirthdayViewModel

@Suppress("UNCHECKED_CAST")
class UsersBirthdayViewModelFactory(private val repository: BirthdayRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UsersBirthdayViewModel::class.java)) {
            return UsersBirthdayViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
