package com.yargisoft.birthify.ui.views.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.yargisoft.birthify.R
import com.yargisoft.birthify.databinding.FragmentWhatIsBirthifyDialogBinding

class WhatIsBirthifyDialogFragment : DialogFragment() {


    private lateinit var binding: FragmentWhatIsBirthifyDialogBinding


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_what_is_birthify_dialog, null, false)
        dialog.setContentView(binding.root)


        binding.closeButton.setOnClickListener{
            dialog.dismiss()
        }


        return dialog
    }

}