package com.qflow.main.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import com.qflow.main.R
import com.qflow.main.core.Failure
import com.qflow.main.core.ScreenState
import com.qflow.main.utils.enums.ValidationFailureType
import com.qflow.main.views.viewmodels.LoginViewModel
import kotlinx.android.synthetic.users.fragment_login.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.qflow.main.views.screenstates.LoginFragmentScreenState as LoginFragmentScreenState

/**
 * Old view used for the login (pending to be deleted)
 * */

class LoginFragment : Fragment() {

    private val viewModel: LoginViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeObservers()
        initializeListeners()
    }

    private fun initializeListeners() {
        initializeButtons()
    }

    private fun initializeButtons() {
        btn_signIn.setOnClickListener {
            view.let {
                val email = inputEmail.text.toString()
                val pass = inputPass.text.toString()
                viewModel.login(pass, email)
            }
        }
        btn_signUp.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_loginFragment_to_signUpFragment)
        }
    }

    private fun handleErrors(failure: Failure?) {
        when (failure) {
            is Failure.ValidationFailure -> {
                loadingComplete()
                when (failure.validationFailureType) {
                    ValidationFailureType.EMAIL_OR_PASSWORD_EMPTY -> {
                        Toast.makeText(
                            this.context, "Email or password empty", Toast.LENGTH_LONG
                        ).show()
                        this.context?.let { ContextCompat.getColor(it, R.color.errorRedColor) }?.let {
                            inputPass.background.setTint(it)
                            inputEmail.background.setTint(it)
                        }
                    }
                }
            }
            else -> {
                loadingComplete()
                Toast.makeText(this.context, "Login was not successful", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUI(screenState: ScreenState<LoginFragmentScreenState>?) {

        when (screenState) {
            ScreenState.Loading -> {
                loading()

            }
            is ScreenState.Render -> renderScreenState(screenState.renderState)
        }
    }

    private fun renderScreenState(renderState: LoginFragmentScreenState) {
        loadingComplete()

        when (renderState) {
            is LoginFragmentScreenState.LoginSuccessful -> {
                view?.findNavController()?.navigate(
                    R.id.action_loginFragment_to_navigation_home
                )
            }
        }
    }

    private fun initializeObservers() {
        viewModel.screenState.observe(::getLifecycle, ::updateUI)
        viewModel.failure.observe(::getLifecycle, ::handleErrors)
    }



    private fun loading(){
        //Make sure you've added the loader to the view
        loading_bar.visibility = View.VISIBLE
    }

    private fun loadingComplete(){
        loading_bar.visibility = View.INVISIBLE
    }



}
