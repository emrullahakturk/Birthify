package com.yargisoft.birthify.ui.views.authfragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
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

    private lateinit var  binding: FragmentFirstPageBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View{

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_first_page, container, false)



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

}