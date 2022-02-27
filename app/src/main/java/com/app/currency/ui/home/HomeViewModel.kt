package com.app.currency.ui.home

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.currency.network.helper.Resource
import com.app.currency.network.helper.SingleLiveEvent
import com.app.currency.model.ApiResponse
import com.app.currency.network.Repository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class HomeViewModel @ViewModelInject constructor(private val repository: Repository) : ViewModel()  {


    //cached
    private val _data = SingleLiveEvent<Resource<ApiResponse>>()

    //public
    val data  =  _data
    val convertedRate = MutableLiveData<Double>()


    //Public function to get the result of conversion
    fun getConvertedData(from: String, to: String, amount: Double) {
        viewModelScope.launch {
            repository.getConvertedData(from, to, amount).collect {
                data.value = it
            }
        }
    }


}