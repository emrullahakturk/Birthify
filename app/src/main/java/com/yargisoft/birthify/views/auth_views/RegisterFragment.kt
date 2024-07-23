package com.yargisoft.birthify.views.auth_views

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.yargisoft.birthify.R
import com.yargisoft.birthify.databinding.FragmentRegisterBinding

import com.yargisoft.birthify.repositories.AuthRepository
import com.yargisoft.birthify.viewmodels.AuthViewModel
import com.yargisoft.birthify.viewmodels.factories.AuthViewModelFactory

class RegisterFragment : Fragment() {

    private lateinit var binding : FragmentRegisterBinding
    private lateinit var viewModel : AuthViewModel
    private lateinit var emailTextInputLayout: TextInputLayout
    private lateinit var emailEditText: TextInputEditText
    private lateinit var registerPassTextInput: TextInputLayout
    private lateinit var registerPasswordEditText: TextInputEditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_register, container, false)

        val repository = AuthRepository(requireContext())
        val factory= AuthViewModelFactory(repository)
        viewModel = ViewModelProvider(this,factory)[AuthViewModel::class.java]

        // Snackbar için view
        val view = (context as Activity).findViewById<View>(android.R.id.content)




        // Register fragment sayfasında girilen e-mail formatını kontrol ediyoruz
        emailTextInputLayout = binding.emailRegisterTextInputLayout
        emailEditText = binding.emailRegisterEditText
        emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = s.toString()
                if (isValidEmail(email)) {
                    emailTextInputLayout.error = null
                    emailTextInputLayout.isErrorEnabled = false //error yazıdı gittiğinde yazıdan kalan boşluk bu kod ile gider
                } else {
                    emailTextInputLayout.error = "Invalid email address"

                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
        //Kutucuk üstünden focus kaldırıldığında hata mesajı da kalkar
        emailEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                emailTextInputLayout.error = ""
                emailTextInputLayout.isErrorEnabled = false
            }
        }
        // Register fragment sayfasında girilen e-mail formatını kontrol ediyoruz



        // Register fragment sayfasında girilen şifre formatını kontrol ediyoruz
        registerPassTextInput = binding.registerPassTextInput
        registerPasswordEditText = binding.registerPasswordEditText
        registerPasswordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val password = s.toString()
                if (isValidPassword(password)) {
                    registerPassTextInput.error = ""
                    registerPassTextInput.isErrorEnabled = false
                } else {
                    registerPassTextInput.error = "Password must be at least 6 characters, include an uppercase letter, a number, and a punctuation mark"
                    registerPassTextInput.isErrorEnabled = true
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
        //Kutucuk üstünden focus kaldırıldığında hata mesajı da kalkar
        registerPasswordEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                registerPassTextInput.error = ""
                registerPassTextInput.isErrorEnabled = false
            }
        }
        // Register fragment sayfasında girilen şifre formatını kontrol ediyoruz




        binding.signInRegisterTv.setOnClickListener {it.findNavController().navigate(R.id.registerToLogin)}
        binding.fabRegister.setOnClickListener { parentFragmentManager.popBackStack() }
        binding.forgotPassRegisterTv.setOnClickListener { it.findNavController().navigate(R.id.registerToForgot) }

        binding.registerButton.setOnClickListener {
            val name = binding.registerFullNameTv.text.toString()
            val email = binding.emailRegisterEditText.text.toString()
            val password = binding.registerPasswordEditText.text.toString()

            if( name.isNotEmpty()
                && email.isNotEmpty()
                && password.isNotEmpty()
                && isValidEmail(email)
                && isValidPassword(password)
                ){

                //user kaydetme fonksiyonunu viewmodeldan çağırıyoruz
                viewModel.registerUser(name,email,password)

                //user kaydedilene kadar view'i invisible edip lottie animasyonunu başlatıyoruz
                binding.registerFragmentTopLayout.visibility = View.INVISIBLE
                binding.registerLottieAnimation.visibility = View.VISIBLE
                binding.registerLottieAnimation.playAnimation()

                Handler(Looper.getMainLooper()).postDelayed({
                    viewModel.registrationState.observe(viewLifecycleOwner) { isSuccess ->
                        if (isSuccess) {
                            Snackbar.make(view,"Registration successfully, please verify your email",Snackbar.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.registerToLogin)
                        } else {
                            Snackbar.make(view,"Registration failed",Snackbar.LENGTH_SHORT).show()
                        }
                    }


                    // Observe işleminden sonra observe'ı kaldırır
                    viewModel.registrationState.removeObservers(viewLifecycleOwner)

                    //animasyonu durdurup view'i visible yapıyoruz
                    binding.registerLottieAnimation.cancelAnimation()
                    binding.registerLottieAnimation.visibility = View.INVISIBLE
                    binding.registerFragmentTopLayout.visibility = View.VISIBLE
                }, 5000)
            }else{
                Snackbar.make(view,"Please correctly fill in all fields",Snackbar.LENGTH_SHORT).show()
            }


        }


        return binding.root
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = Patterns.EMAIL_ADDRESS
        return emailPattern.matcher(email).matches()
    }
    private fun isValidPassword(password: String): Boolean {
        val passwordPattern = Regex("^(?=.*[A-Z])(?=.*[0-9])(?=.*\\W).{6,}$")
        return passwordPattern.matches(password)
    }


}