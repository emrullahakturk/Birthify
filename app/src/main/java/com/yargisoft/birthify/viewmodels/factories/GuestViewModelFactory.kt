package com.yargisoft.birthify.viewmodels.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yargisoft.birthify.repositories.GuestRepository
import com.yargisoft.birthify.viewmodels.GuestBirthdayViewModel

@Suppress("UNCHECKED_CAST")
class GuestViewModelFactory(private val repository: GuestRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GuestBirthdayViewModel::class.java)) {
            return GuestBirthdayViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}