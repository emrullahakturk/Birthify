package com.yargisoft.birthify.views.auth_views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.yargisoft.birthify.R
import com.yargisoft.birthify.databinding.FragmentFirstPageBinding

class FirstPageFragment : Fragment() {

    private lateinit var  binding: FragmentFirstPageBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View{

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_first_page, container, false)

        binding.signInButton.setOnClickListener {
            it.findNavController().navigate(R.id.firstToLogin)
        }

        binding.crAccountTv.setOnClickListener {
            it.findNavController().navigate(R.id.firstToRegister)
        }
        binding.continueWithoutTv.setOnClickListener {
            findNavController().navigate(R.id.firstToMain)
        }

        return  binding.root
    }

}