package com.app.currency.network.helper

import com.app.currency.BuildConfig

/**
 * All URL listed in this class
 */

class EndPoints {

    companion object {

        //Base URL

        const val BASE_URL = "https://api.getgeoapi.com/api/v2/currency/"

        //API KEY
        const val API_KEY = BuildConfig.API_TOKEN

        //COVERT URL
        const val  CONVERT_URL = "convert"

        //Historical URL
        const val  HISTORICAL_URL = "historical"

    }

}