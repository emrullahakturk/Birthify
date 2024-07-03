package com.yargisoft.birthify.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.yargisoft.birthify.R
import com.yargisoft.birthify.databinding.FragmentMainPageBinding
import java.sql.RowId


class MainPageFragment : Fragment() {

    private lateinit var  binding: FragmentMainPageBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_main_page, container, false)

        binding.signInButton.setOnClickListener {
            it.findNavController().navigate(R.id.mainToLogin)
        }

        binding.crAccountTv.setOnClickListener {
            it.findNavController().navigate(R.id.mainToRegister)
        }


        return  binding.root
    }

}