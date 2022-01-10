package com.amsavarthan.security.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.amsavarthan.security.data.database.guardian.GuardianDao
import com.amsavarthan.security.data.database.user.UserDao

class MyViewModelFactory(
    private val guardianDao: GuardianDao? = null,
    private val userDao: UserDao? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(UserViewModel::class.java) -> {
                UserViewModel(userDao!!) as T
            }
            modelClass.isAssignableFrom(GuardianViewModel::class.java) -> {
                GuardianViewModel(guardianDao!!) as T
            }
            else -> throw IllegalStateException("ViewModel not found")
        }
    }
}