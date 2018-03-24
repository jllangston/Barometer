package com.jl.barometer.rxsensor

import com.jl.barometer.data.BarometerReading
import io.reactivex.Observable

/**
 * Created by jl on 3/18/18.
 *
 * Provides sensor contract
 */
interface  SensorReadContract {

    companion object {
        const val N_READINGS_TO_AVERAGE = 5
    }

    fun getPressureReading(nReadingsToAverage: Int = N_READINGS_TO_AVERAGE)
            : Observable<BarometerReading>

}