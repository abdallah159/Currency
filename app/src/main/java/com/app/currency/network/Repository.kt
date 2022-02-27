package com.app.currency.network

import com.app.currency.network.helper.Resource
import com.app.currency.model.ApiResponse
import com.app.currency.network.RemoteDataSource
import com.app.currency.network.BaseDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject


class Repository @Inject constructor(private val remoteDataSource: RemoteDataSource) : BaseDataSource() {

    //Using coroutines flow to get the response from
    suspend fun getConvertedData(from: String, to: String, amount: Double): Flow<Resource<ApiResponse>> {
        return flow {
            emit(safeApiCall { remoteDataSource.getConvertedRate(from, to, amount) })
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getCurrencyHistory(date : String, from: String, to: String, amount: Double): Flow<Resource<ApiResponse>> {
        return flow {
            emit(safeApiCall { remoteDataSource.getCurrencyHistory(date,from, to, amount) })
        }.flowOn(Dispatchers.IO)
    }

}