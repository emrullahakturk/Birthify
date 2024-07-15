package com.yargisoft.birthify.views.AuthViews

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
import com.yargisoft.birthify.repositories.AuthRepository
import com.yargisoft.birthify.viewmodels.AuthViewModel
import com.yargisoft.birthify.viewmodels.factories.AuthViewModelFactory


class LoginPageFragment : Fragment() {

    private lateinit var binding: FragmentLoginPageBinding
    private lateinit var viewModel: AuthViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login_page, container, false)
        // Inflate the layout for this fragment

        val repository = AuthRepository(requireContext())
        val factory = AuthViewModelFactory(repository)
        viewModel = ViewModelProvider(this,factory).get(AuthViewModel::class.java)

        binding.fabWelcome.setOnClickListener {
            it.findNavController().popBackStack()
        }

        binding.forgotPassLoginTv.setOnClickListener { it.findNavController().navigate(R.id.loginToForgot) }
        binding.signUpLoginTv.setOnClickListener { it.findNavController().navigate(R.id.loginToRegister) }

        binding.loginButton.setOnClickListener {

            val email = binding.loginPageEmailTv.text.toString()
            val password = binding.loginPagePassTv.text.toString()
            val isChecked = binding.rememberCBox.isChecked

            if (email.isNotEmpty() && password.isNotEmpty()) {
                viewModel.loginUser(email, password,isChecked)

                viewModel.loginState.observe(viewLifecycleOwner, Observer { isSuccess ->
                    if (isSuccess) {
                        // Başarılı giriş işlemi
                        Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                        it.findNavController().navigate(R.id.loginToMain)
                    } else {
                        // Başarısız giriş işlemi
                        Toast.makeText(context, "Login failed", Toast.LENGTH_SHORT).show()
                    }
                })

            } else {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }

        }

        return binding.root
    }



}