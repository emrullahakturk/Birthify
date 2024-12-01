package com.yargisoft.birthify.ui.views.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.yargisoft.birthify.R
import com.yargisoft.birthify.databinding.FragmentNotifyTimeBottomSheetDialogBinding

class NotifyTimeBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private lateinit var binding : FragmentNotifyTimeBottomSheetDialogBinding
    private var onOptionSelected: ((String) -> Unit)? = null

    fun setOnOptionSelectedListener(listener: (String) -> Unit) {
        onOptionSelected = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_notify_time_bottom_sheet_dialog, container, false)



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       binding.option1.setOnClickListener {
            onOptionSelected?.invoke("On the day")
            dismiss()
        }
        binding.option2.setOnClickListener {
            onOptionSelected?.invoke("1 day ago")
            dismiss()
        }
        binding.option3.setOnClickListener {
            onOptionSelected?.invoke("1 week ago")
            dismiss()
        }
        binding.option4.setOnClickListener {
            onOptionSelected?.invoke("1 month ago")
            dismiss()
        }
    }
}
