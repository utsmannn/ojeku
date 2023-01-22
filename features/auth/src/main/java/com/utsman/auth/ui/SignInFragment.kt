package com.utsman.auth.ui

import android.os.Bundle
import com.utsman.auth.databinding.FragmentSignInBinding
import com.utsman.core.extensions.onFailure
import com.utsman.core.extensions.onSuccess
import com.utsman.navigation.activityNavigationCust
import com.utsman.navigation.activityNavigationDriver
import com.utsman.utils.BindingFragment
import com.utsman.utils.snackBar
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SignInFragment : BindingFragment<FragmentSignInBinding>() {
    private val authViewModel: AuthViewModel by sharedViewModel()

    private val type: String by lazy {
        arguments?.getString("type") ?: "customer"
    }

    override fun inflateBinding(): FragmentSignInBinding {
        return FragmentSignInBinding.inflate(layoutInflater)
    }

    override fun onCreateBinding(savedInstanceState: Bundle?) {
        binding.btnSignIn.setOnClickListener {
            val username = binding.edUsername.text.toString()
            val password = binding.edPassword.text.toString()

            authViewModel.signIn(username, password)
        }

        authViewModel.signInState.observe(this) { state ->
            state.onFailure {
                this.printStackTrace()
                binding.snackBar(this.message)
            }

            state.onSuccess {
                authViewModel.saveToken(this)
                if (type == "customer") {
                    activityNavigationCust().mainActivity(context)
                } else {
                    activityNavigationDriver().mainActivity(context)
                }
                activity?.finish()
            }
        }
    }
}