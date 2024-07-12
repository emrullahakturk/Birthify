package com.yargisoft.birthify.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.yargisoft.birthify.R
import com.yargisoft.birthify.databinding.FragmentLoginPageBinding
import com.yargisoft.birthify.viewmodels.AuthViewModel


class LoginPageFragment : Fragment() {

    private lateinit var binding: FragmentLoginPageBinding
    private lateinit var viewModel: AuthViewModel



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login_page, container, false)
        // Inflate the layout for this fragment

        viewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        binding.fabWelcome.setOnClickListener {
            it.findNavController().popBackStack()
        }

        binding.forgotPassLoginTv.setOnClickListener { it.findNavController().navigate(R.id.loginToForgot) }
        binding.signUpLoginTv.setOnClickListener { it.findNavController().navigate(R.id.loginToRegister) }

        binding.loginButton.setOnClickListener {
            val email = binding.loginPageEmailTv.text.toString()
            val password = binding.loginPagePassTv.text.toString()

            viewModel.loginUser(email, password)

            viewModel.loginState.observe(viewLifecycleOwner, Observer { isSuccess ->
                if (isSuccess) {
                it.findNavController().navigate(R.id.loginToForgot)                    // Başarılı girişten sonra yapılacak işlemler
                } else {
                    Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show()
                }
            })
        }




        return binding.root
    }



}