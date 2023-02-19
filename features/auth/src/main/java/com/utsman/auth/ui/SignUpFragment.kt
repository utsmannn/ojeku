package com.utsman.auth.ui

import android.os.Bundle
import androidx.core.view.isVisible
import com.utsman.auth.databinding.FragmentSignUpBinding
import com.utsman.core.extensions.onFailure
import com.utsman.core.extensions.onSuccess
import com.utsman.navigation.activityNavigationCust
import com.utsman.navigation.activityNavigationDriver
import com.utsman.utils.BindingFragment
import com.utsman.utils.snackBar
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SignUpFragment : BindingFragment<FragmentSignUpBinding>() {

    private val type: String by lazy {
        arguments?.getString("type") ?: "customer"
    }

    private val authViewModel: AuthViewModel by sharedViewModel()

    override fun inflateBinding(): FragmentSignUpBinding {
        return FragmentSignUpBinding.inflate(layoutInflater)
    }

    override fun onCreateBinding(savedInstanceState: Bundle?) {
        binding.btnSignUp.setOnClickListener {
            val username = binding.edUsername.text.toString()
            val password = binding.edPassword.text.toString()
            val vehiclesNumber = binding.edVehiclesNumber.text.toString()

            if (type == "customer") {
                binding.edVehiclesNumber.isVisible = false
                authViewModel.signUpCustomer(username, password)
            } else {
                authViewModel.signUpDriver(username, password, vehiclesNumber)
            }
        }

        authViewModel.signUpState.observe(this) { state ->
            state.onFailure {
                this.printStackTrace()
                binding.snackBar(this.message)
            }

            state.onSuccess {
                if (type == "customer") {
                    activityNavigationCust().authActivityCustomer(context)
                } else {
                    activityNavigationDriver().authActivityDriver(context)
                }
                activity?.finish()
            }
        }
    }
}