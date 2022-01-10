package com.amsavarthan.security.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amsavarthan.security.data.database.user.User
import com.amsavarthan.security.data.database.user.UserDao
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class UserViewModel(
    private val userDao: UserDao
) : ViewModel() {

    private var _user: MutableLiveData<User> = MutableLiveData(null)
    val user: LiveData<User> = _user

    init {
        viewModelScope.launch {
            userDao.getUserDetails().collect { student ->
                _user.value = student
            }
        }
    }

    fun insertUser(user: User) {
        viewModelScope.launch {
            userDao.insertUser(user)
        }
    }

}