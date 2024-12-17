package com.yargisoft.birthify.ui.views.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.yargisoft.birthify.databinding.FragmentNotifyTimeBottomSheetDialogBinding
import javax.inject.Inject

class NotifyTimeBottomSheetDialogFragment @Inject constructor() : BottomSheetDialogFragment() {

    private var _binding: FragmentNotifyTimeBottomSheetDialogBinding? = null
    private val binding get() = _binding!!

    private var onOptionSelected: ((String) -> Unit)? = null

    fun setOnOptionSelectedListener(listener: (String) -> Unit) {
        onOptionSelected = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotifyTimeBottomSheetDialogBinding.inflate(inflater, container, false)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
