package com.yargisoft.birthify.views

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.yargisoft.birthify.R
import com.yargisoft.birthify.adapters.DeletedBrithdayAdapter
import com.yargisoft.birthify.databinding.FragmentDeletedBirthdayDetailBinding
import com.yargisoft.birthify.databinding.FragmentTrashBinBinding
import com.yargisoft.birthify.repositories.BirthdayRepository
import com.yargisoft.birthify.sharedpreferences.SharedPreferencesManager
import com.yargisoft.birthify.viewmodels.BirthdayViewModel
import com.yargisoft.birthify.viewmodels.factories.BirthdayViewModelFactory

class DeletedBirthdayDetailFragment : Fragment() {
    private lateinit var binding: FragmentDeletedBirthdayDetailBinding
    private lateinit var viewModel: BirthdayViewModel
    private lateinit var repository: BirthdayRepository
    private lateinit var factory: BirthdayViewModelFactory
    private val deletedBirthday : DeletedBirthdayDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        repository = BirthdayRepository(requireContext())
        factory = BirthdayViewModelFactory(repository)
        viewModel = ViewModelProvider(this,factory)[BirthdayViewModel::class]

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_deleted_birthday_detail , container, false)
        binding.birthday= deletedBirthday.birthday


        binding.reSaveDeletedBirthday.setOnClickListener {
            showDeleteConfirmationDialog()
        }


        // Inflate the layout for this fragment
        return binding.root
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Confirm Re Saving")
            .setMessage("Are you sure you want to re save this birthday?")
            .setPositiveButton("Yes") { _, _ ->
                viewModel.reSaveDeletedBirthday(deletedBirthday.birthday.id, deletedBirthday.birthday)
                viewModel.reSaveDeletedBirthdayState.observe(viewLifecycleOwner, Observer { isSuccess ->
                    if (isSuccess) {
                        Toast.makeText(context, "Successfully re saved", Toast.LENGTH_SHORT).show()
                        findNavController().navigateUp()
                    } else {
                        Toast.makeText(context, "Failed to re save deleted birthday", Toast.LENGTH_SHORT).show()
                    }
                })
            }
            .setNegativeButton("No", null)
            .show()
    }

}