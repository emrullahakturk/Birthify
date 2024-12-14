package com.yargisoft.birthify.ui.views.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.yargisoft.birthify.FrequentlyUsedFunctions
import com.yargisoft.birthify.R
import com.yargisoft.birthify.data.repositories.BirthdayRepository
import com.yargisoft.birthify.databinding.FragmentAuthDeletedBirthdayDetailBinding
import com.yargisoft.birthify.ui.viewmodels.BirthdayViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint

class DeletedBirthdayDetailFragment : Fragment() {
    private lateinit var binding: FragmentAuthDeletedBirthdayDetailBinding

    private val birthdayViewModel: BirthdayViewModel by viewModels()
    private val deletedBirthday: DeletedBirthdayDetailFragmentArgs by navArgs()

    @Inject
    lateinit var birthdayRepository: BirthdayRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_auth_deleted_birthday_detail,
            container,
            false
        )

        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.deletedBirthdayDetailFragment, true)
            .build()


        //detayı gösterilmek istenen doğum gününü kutucuklara yansıtıyoruz
        binding.birthday = deletedBirthday.birthday


        binding.fabBackButton.setOnClickListener { findNavController().popBackStack() }


        binding.reSaveButton.setOnClickListener {
            FrequentlyUsedFunctions.showConfirmationDialog(
                binding.root,
                requireContext(),
                birthdayViewModel,
                deletedBirthday.birthday,
                binding.threePointAnimation,
                viewLifecycleOwner,
                "re_save",
                findNavController(),
                R.id.deletedDetailToTrashBin,
                navOptions
            )
        }


        binding.permanentlyDeleteButton.setOnClickListener {
            FrequentlyUsedFunctions.showConfirmationDialog(
                binding.root,
                requireContext(),
                birthdayViewModel,
                deletedBirthday.birthday,
                binding.threePointAnimation,
                viewLifecycleOwner,
                "permanently",
                findNavController(),
                R.id.deletedDetailToTrashBin,
                navOptions
            )
        }







        return binding.root
    }

}