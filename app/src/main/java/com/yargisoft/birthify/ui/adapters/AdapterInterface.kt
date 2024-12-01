package com.yargisoft.birthify.ui.adapters

import com.yargisoft.birthify.data.models.Birthday

interface AdapterInterface {
    fun updateData(newBirthdays: List<Birthday>)
}