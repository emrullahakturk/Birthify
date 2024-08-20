package com.yargisoft.birthify.views.dialogs

import android.app.Dialog
import android.os.Bundle
import android.widget.ImageButton
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.yargisoft.birthify.R
import com.yargisoft.birthify.databinding.FragmentPrivacyPolicyDialogBinding


class PrivacyPolicyDialogFragment : DialogFragment() {


    private lateinit var binding: FragmentPrivacyPolicyDialogBinding


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_privacy_policy_dialog, null, false)
        dialog.setContentView(binding.root)

        val closeButton = dialog.findViewById<ImageButton>(R.id.closeButton)
        closeButton.setOnClickListener {
            dialog.dismiss()
        }


        return dialog
    }
}