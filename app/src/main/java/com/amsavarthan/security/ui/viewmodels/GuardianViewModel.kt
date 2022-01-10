package com.amsavarthan.security.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amsavarthan.security.data.database.guardian.Guardian
import com.amsavarthan.security.data.database.guardian.GuardianDao
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class GuardianViewModel(
    private val guardianDao: GuardianDao
) : ViewModel() {

    private val _guardians: MutableLiveData<List<Guardian>> = MutableLiveData(listOf())
    val guardians: LiveData<List<Guardian>> get() = _guardians

    init {
        viewModelScope.launch {
            guardianDao.getAllGuardianDetailsFlow().collect { guardians ->
                _guardians.value = guardians
            }
        }
    }

    fun deleteGuardian(guardian: Guardian) {
        viewModelScope.launch {
            guardianDao.deleteGuardian(guardian)
        }
    }

    fun insertGuardian(guardian: Guardian) {
        viewModelScope.launch {
            guardianDao.insertGuardian(guardian)
        }
    }

}