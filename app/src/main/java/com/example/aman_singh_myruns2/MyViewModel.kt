package com.example.aman_singh_myruns2

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


//From Canvas
class MyViewModel: ViewModel() {
    val userImage = MutableLiveData<Bitmap>()
}