package com.qflow.main.views.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.qflow.main.core.ScreenState
import com.qflow.main.domain.local.database.user.UserDB
import com.qflow.main.domain.local.database.AppDatabase
import com.qflow.main.views.screenstates.HomeFragmentScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job


/**
 * Old ViewModel for the profileFragment
 * */
class HomeViewModel(
    private val appDatabase: AppDatabase
) : ViewModel() {

    private val _currentUser = MutableLiveData<UserDB>()
    val currentUserDB: LiveData<UserDB>
        get() = _currentUser

    private val _screenState: MutableLiveData<ScreenState<HomeFragmentScreenState>> = MutableLiveData()
    val screenState: LiveData<ScreenState<HomeFragmentScreenState>>
        get() = _screenState

    private var job = Job()
    private var coroutineScope = CoroutineScope(Dispatchers.Default + job)


    private fun handleUserCreated(id: String) {
        this._screenState.value =
            ScreenState.Render(HomeFragmentScreenState.AccessHome(id))
    }

}