package com.yargisoft.birthify.ui.views.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.yargisoft.birthify.BirthdaySortFunctions.showSortMenu
import com.yargisoft.birthify.FrequentlyUsedFunctions
import com.yargisoft.birthify.R
import com.yargisoft.birthify.data.repositories.BirthdayRepository
import com.yargisoft.birthify.databinding.FragmentAuthPastBirthdaysBinding
import com.yargisoft.birthify.ui.adapters.PastBirthdayAdapter
import com.yargisoft.birthify.ui.viewmodels.BirthdayViewModel
import com.yargisoft.birthify.utils.NetworkConnectionObserver
import com.yargisoft.birthify.utils.sharedpreferences.UserSharedPreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint

class PastBirthdaysFragment : Fragment() {

    private lateinit var binding: FragmentAuthPastBirthdaysBinding
    private val birthdayViewModel: BirthdayViewModel by viewModels()

    @Inject
    lateinit var adapter: PastBirthdayAdapter

    @Inject
    lateinit var networkConnectionObserver: NetworkConnectionObserver

    @Inject
    lateinit var birthdayRepository: BirthdayRepository

    @Inject
    lateinit var userSharedPreferences: UserSharedPreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_auth_past_birthdays,
            container,
            false
        )

        networkConnectionObserver.isConnected.observe(viewLifecycleOwner) { isConnected ->
            if (userSharedPreferences.getUserCredentials().second != "guest") {
                if (isConnected) {
                    //Main Page Açıldığında firebase üzerindeki doğum günlerini bday shared pref'e aktararak senkronize etmiş oluyoruz
                    birthdayViewModel.getBirthdaysFromFirebase(
                        userSharedPreferences.getUserId(),
                        "past_birthdays"
                    )
                }
            }
        }

        birthdayViewModel.getBirthdays("past_birthdays")


        val adapterList = birthdayViewModel.birthdayList.value ?: emptyList()
        adapter.updateData(adapterList.sortedByDescending { it.recordedDate })


        //Doğum günlerini viewmodel içindeki live datadan observe ederek ekrana yansıtıyoruz
        birthdayViewModel.birthdayList.observe(viewLifecycleOwner) { birthdays ->
            adapter.updateData(birthdays)
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter

        //Search edittext'i ile doğum günü arama ekliyoruz
        binding.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                FrequentlyUsedFunctions.filterBirthdays(s.toString(), birthdayViewModel, adapter)
            }

            override fun afterTextChanged(s: Editable?) {}
        })





        binding.fabBackPastBirthdays.setOnClickListener {
            findNavController().popBackStack()
        }



        binding.sortButton.setOnClickListener {
            showSortMenu(it, requireContext(), adapter, birthdayViewModel)
        }


        return binding.root
    }

    override fun onResume() {
        super.onResume()
        birthdayViewModel.getBirthdaysFromFirebase(
            userSharedPreferences.getUserId(),
            "past_birthdays"
        )
        birthdayViewModel.getBirthdays("past_birthdays")
    }
}