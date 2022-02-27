package com.app.currency.ui.historical

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.currency.model.ApiResponse
import com.app.currency.network.Repository
import com.app.currency.network.helper.Resource
import com.app.currency.network.helper.SingleLiveEvent
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HistoricalViewModel @ViewModelInject constructor(private val repository: Repository) :
    ViewModel() {


    //cached
    private val _data = SingleLiveEvent<Resource<MutableList<ApiResponse>>>()

    //public
    val data = _data

    val sdf = SimpleDateFormat("yyyy-MM-dd")
    val c = Calendar.getInstance()
    private var datesData: MutableList<ApiResponse> = mutableListOf()
    private var daysCount = 1
    fun getCurrencyHistory(from: String, to: String, amount: Double) {

        val dDate = sdf.format(c.time)

        viewModelScope.launch {
            repository.getCurrencyHistory(dDate, from, to, amount).collect {
                it.data?.let { it1 -> datesData.add(it1) }
                c.add(Calendar.HOUR, -24)
                daysCount++
                if(daysCount!=30){
                    getCurrencyHistory(from,to,amount)
                }else{
                    data.value = Resource(Resource.Status.SUCCESS,datesData,"")
                }
            }
        }

    }


}