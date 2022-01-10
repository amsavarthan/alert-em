package com.amsavarthan.security.ui.views.main

import android.view.View

interface Handler {
    fun onButtonThumbLongPressed(view: View): Boolean
    fun onSettingsButtonClicked(view: View)
    fun goToHelplineNumbersFragment(view: View)
}