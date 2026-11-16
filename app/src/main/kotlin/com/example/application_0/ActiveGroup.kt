package com.example.application_0

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object ActiveGroup {
    private val _groupId = MutableStateFlow<String?>(null)
    val groupId = _groupId.asStateFlow()

    fun select(groupId: String) {
        _groupId.value = groupId
    }

    fun clear() {
        _groupId.value = null
    }
}
