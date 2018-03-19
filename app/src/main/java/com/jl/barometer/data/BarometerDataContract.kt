package com.jl.barometer.data

import io.reactivex.Flowable

/**
 * Created by jl on 2/3/18.
 */
interface BarometerDataContract {

    fun addReading(reading: BarometerReading)
    fun getAllData() : Flowable<List<BarometerReading>>

}