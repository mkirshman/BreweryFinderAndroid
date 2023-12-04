package com.mkmusical.metalkindred.breweryfinder.data

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface BreweryService {
    @GET("GetBreweries")
    fun getBreweries(
        @Query("by_name") name: String?,
        @Query("by_state") state: String?,
        @Query("postalCode") postalCode: String?,
        @Query("by_type") type: String?,
    ): Call<List<Brewery>>
}