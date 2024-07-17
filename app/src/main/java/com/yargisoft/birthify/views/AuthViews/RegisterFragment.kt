package com.yargisoft.birthify.views.AuthViews

import android.opengl.Visibility
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.yargisoft.birthify.R
import com.yargisoft.birthify.databinding.FragmentRegisterBinding

import com.yargisoft.birthify.repositories.AuthRepository
import com.yargisoft.birthify.viewmodels.AuthViewModel
import com.yargisoft.birthify.viewmodels.factories.AuthViewModelFactory

class RegisterFragment : Fragment() {

    private lateinit var binding : FragmentRegisterBinding
    private lateinit var viewModel : AuthViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment


        val repository = AuthRepository(requireContext())
        val factory= AuthViewModelFactory(repository)
        viewModel = ViewModelProvider(this,factory).get(AuthViewModel::class.java)



        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_register, container, false)
        binding.signInRegisterTv.setOnClickListener {it.findNavController().navigate(R.id.registerToLogin)}
        binding.fabRegister.setOnClickListener { parentFragmentManager.popBackStack() }
        binding.forgotPassRegisterTv.setOnClickListener { it.findNavController().navigate(R.id.registerToForgot) }

        binding.registerButton.setOnClickListener {
            val name = binding.registerFullNameTv.text.toString()
            val email = binding.registerEmailTv.text.toString()
            val password = binding.registerPassTv.text.toString()
            viewModel.registerUser(name,email,password)

            binding.registerFragmentProgress.visibility = View.VISIBLE

            Handler(Looper.getMainLooper()).postDelayed({
                viewModel.registrationState.observe(viewLifecycleOwner, Observer { isSuccess ->
                    if (isSuccess) {
                        Toast.makeText(context, "Registration succesfully", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.registerToLogin)
                    } else {
                        Toast.makeText(context, "Registration Failed", Toast.LENGTH_SHORT).show()
                    }
                })
                // Observe işleminden sonra observe'ı kaldır
                viewModel.registrationState.removeObservers(viewLifecycleOwner)
                binding.registerFragmentProgress.visibility = View.INVISIBLE

            }, 5000)





        }






        return binding.root
    }



}