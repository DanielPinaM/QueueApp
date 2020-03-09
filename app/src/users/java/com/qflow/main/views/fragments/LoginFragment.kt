package com.qflow.main.views.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import com.qflow.main.R
import com.qflow.main.core.Failure
import com.qflow.main.core.ScreenState
import com.qflow.main.utils.enums.ValidationFailureType
import com.qflow.main.views.viewmodels.LoginViewModel
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
        initializeObservers()
        initializeListeners()
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    private fun initializeListeners() {
        viewModel.screenState.observe(viewLifecycleOwner, Observer {
            updateUi(it)
        })
        viewModel.failure.observe(::getLifecycle, ::handleErrors)
    }

    private fun handleErrors(failure: Failure?) {
        when (failure) {
            is Failure.ValidationFailure -> {
                when (failure.validationFailureType) {
                    ValidationFailureType.EMAIL_OR_PASSWORD_EMPTY -> {
                        //TODO añadir aqui que hacer cuando el validador de fallo

                    }
                }
            }
        }
    }


    private fun updateUi(screenState: ScreenState<LoginFragmentScreenState>?) {

        when (screenState) {
            ScreenState.Loading -> {
            }
            is ScreenState.Render -> renderScreenState(screenState.renderState)
        }
    }

    private fun renderScreenState(renderState: LoginFragmentScreenState) {

        when (renderState) {
            is LoginFragmentScreenState.LoginSuccessful -> {
                Toast.makeText(this.context, renderState.id.toString(), Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun initializeObservers() {
        viewModel.screenState.observe(viewLifecycleOwner, Observer {
            updateUi(it)
        })
    }


}
