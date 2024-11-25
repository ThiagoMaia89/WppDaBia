package com.example.wppdabia.ui.home

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wppdabia.data.LastMessageCardViewData
import com.example.wppdabia.network.Remote
import com.example.wppdabia.network.RemoteImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

class HomeViewModel @Inject constructor() : ViewModel() {
    private val _lastMessageCardViews = MutableLiveData<List<LastMessageCardViewData>>()
    val lastMessageCardViews: LiveData<List<LastMessageCardViewData>> = _lastMessageCardViews
}