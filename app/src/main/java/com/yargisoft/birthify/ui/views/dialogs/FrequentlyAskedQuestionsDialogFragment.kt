package com.yargisoft.birthify.ui.views.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.yargisoft.birthify.databinding.FragmentFrequentlyAskedQuestionsDialogBinding


class FrequentlyAskedQuestionsDialogFragment : DialogFragment() {

    private var _binding: FragmentFrequentlyAskedQuestionsDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        _binding = FragmentFrequentlyAskedQuestionsDialogBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)

        binding.closeButton.setOnClickListener {
            dialog.dismiss()
        }

        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
