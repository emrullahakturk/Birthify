package com.yargisoft.birthify.ui.views.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.yargisoft.birthify.R
import com.yargisoft.birthify.databinding.FragmentFirstPageBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FirstPageFragment : Fragment() {

    val navOptions = NavOptions.Builder()
        .setPopUpTo(R.id.firstPageFragment, true)
        .build()

    private var _binding: FragmentFirstPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View{

        _binding = FragmentFirstPageBinding.inflate(inflater, container, false)


        binding.signInButton.setOnClickListener {
            navigateToFragmentAndClearStack(findNavController(),R.id.firstPageFragment,R.id.firstToLogin)
        }

        binding.createAccountButton.setOnClickListener {
            navigateToFragmentAndClearStack(findNavController(),R.id.firstPageFragment,R.id.firstToRegister)
        }

        return  binding.root
    }

    private fun navigateToFragmentAndClearStack(navController: NavController, currentFragmentId: Int, targetFragmentId: Int) {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(currentFragmentId, inclusive = true)
            .build()

        navController.navigate(targetFragmentId, null, navOptions)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}