package com.example.application_0

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

abstract class AuthenticatedViewModel : ViewModel() {
    protected fun observeAuthenticatedUser(onChanged: (AuthenticatedUser?) -> Unit) {
        viewModelScope.launch {
            Authentication.currentUser.collect(onChanged)
        }
    }
}
