package com.yargisoft.birthify.views.authentication_views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.yargisoft.birthify.R
import com.yargisoft.birthify.databinding.FragmentFirstPageBinding
import com.yargisoft.birthify.sharedpreferences.UserSharedPreferencesManager

class FirstPageFragment : Fragment() {
    private lateinit var userSharedPreferences: UserSharedPreferencesManager
    val navOptions = NavOptions.Builder()
        .setPopUpTo(R.id.firstPageFragment, true)
        .build()

    private lateinit var  binding: FragmentFirstPageBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View{

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_first_page, container, false)


        userSharedPreferences = UserSharedPreferencesManager(requireContext())
        //sayfa açıldığında user bilgilerini sıfırlar
        userSharedPreferences.clearUserSession()



        binding.signInButton.setOnClickListener {
            navigateToFragmentAndClearStack(findNavController(),R.id.firstPageFragment,R.id.firstToLogin)
            userSharedPreferences.clearUserSession()
        }

        binding.crAccountTv.setOnClickListener {
            userSharedPreferences.clearUserSession()
            navigateToFragmentAndClearStack(findNavController(),R.id.firstPageFragment,R.id.firstToRegister)
        }
        binding.continueWithoutTv.setOnClickListener {
            userSharedPreferences.clearUserSession()
            navigateToFragmentAndClearStack(findNavController(),R.id.firstPageFragment,R.id.firstToGuestNavGraph)
            userSharedPreferences.saveAsGuest()
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