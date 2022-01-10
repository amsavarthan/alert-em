package com.amsavarthan.security.ui.views.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.amsavarthan.security.App
import com.amsavarthan.security.R
import com.amsavarthan.security.data.database.user.Gender
import com.amsavarthan.security.data.database.user.User
import com.amsavarthan.security.databinding.FragmentUserDetailsBinding
import com.amsavarthan.security.ui.viewmodels.MyViewModelFactory
import com.amsavarthan.security.ui.viewmodels.UserViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.MaterialFadeThrough
import com.google.android.material.transition.MaterialSharedAxis

class UserDetailsFragment : Fragment(), Handler {

    private var _binding: FragmentUserDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: UserViewModel by viewModels {
        MyViewModelFactory(
            userDao = (activity?.application as App).database.userDao()
        )
    }

    private val gender = listOf("Male", "Female", "Other")
    private val genderAdapter by lazy {
        ArrayAdapter(
            requireContext(),
            R.layout.item_dropdown,
            gender
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        exitTransition = MaterialFadeThrough()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserDetailsBinding.inflate(inflater, container, false)
        binding.run {
            handler = this@UserDetailsFragment
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.run {
            viewmodel = viewModel
            genderAdapter = this@UserDetailsFragment.genderAdapter
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onNextButtonClicked(view: View) {
        val navController = findNavController()
        with(binding) {

            val name = nameEditText.text.toString()
            val phone = phoneEditText.text.toString()
            val gender = genderEditText.text.toString()

            if (name.isBlank() || phone.isBlank() || gender.isBlank()) {
                snackBar("Enter all required details")
                return
            }

            if (phone.length != 10) {
                snackBar("Invalid phone number")
                return
            }

            val user = User(
                name = name,
                phoneNumber = phone,
                gender = Gender.valueOf(gender.uppercase())
            )
            viewModel.insertUser(user)

            val direction =
                UserDetailsFragmentDirections.actionUserDetailsFragmentToGuardianDetailsFragment()
            navController.navigate(direction)
        }
    }

    private fun snackBar(message: String, duration: Int = Snackbar.LENGTH_SHORT) {
        Snackbar.make(binding.root, message, duration).show()
    }

}