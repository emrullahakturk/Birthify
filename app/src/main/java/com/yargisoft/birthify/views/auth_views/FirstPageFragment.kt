package com.yargisoft.birthify.views.auth_views

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.yargisoft.birthify.R
import com.yargisoft.birthify.databinding.FragmentFirstPageBinding

class FirstPageFragment : Fragment() {

    private lateinit var  binding: FragmentFirstPageBinding
  /*  private lateinit var userPreferences : UserSharedPreferencesManager
    private lateinit var birthdayPreferences : BirthdaySharedPreferencesManager*/
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View{

     /*   //preferences
        userPreferences = UserSharedPreferencesManager(requireContext())
        birthdayPreferences = BirthdaySharedPreferencesManager(requireContext())
*/

        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_first_page, container, false)

        binding.signInButton.setOnClickListener {
            it.findNavController().navigate(R.id.firstToLogin)
        }

        binding.crAccountTv.setOnClickListener {
            it.findNavController().navigate(R.id.firstToRegister)
        }

     /*   binding.continueWithoutTv.setOnClickListener {
           showWihtoutSigninDialog()
        }*/


        return  binding.root
    }


  /*  private fun showWihtoutSigninDialog(){
        AlertDialog.Builder(requireContext())
            .setTitle("Confirm Without Signing")
            .setMessage("Sign up to be able to use your data on all devices you log in to. \nAre you sure you want to continue without logging in?")
            .setPositiveButton("Yes") { _, _ ->
                userPreferences.clearUserSession()
                birthdayPreferences.clearBirthdays()
                findNavController().navigate(R.id.firstToMain)

            }
            .setNegativeButton("No",null)
            .show()
    }*/

}