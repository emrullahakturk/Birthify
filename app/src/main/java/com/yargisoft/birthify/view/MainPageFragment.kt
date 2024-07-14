package com.yargisoft.birthify.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.yargisoft.birthify.R
import com.yargisoft.birthify.databinding.FragmentMainPageBinding


class MainPageFragment : Fragment() {
    private lateinit var binding: FragmentMainPageBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_main_page, container, false)

        binding.fabMainPageAddBDay.setOnClickListener { it.findNavController().navigate(R.id.mainToAddBirthday) }

        // Inflate the layout for this fragment
        return binding.root
    }

}