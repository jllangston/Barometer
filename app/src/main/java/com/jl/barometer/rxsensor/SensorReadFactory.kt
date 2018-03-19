package com.jl.barometer.rxsensor

import android.content.Context
import android.hardware.Sensor
import com.github.pwittchen.reactivesensors.library.ReactiveSensorFilter
import com.github.pwittchen.reactivesensors.library.ReactiveSensors
import com.jl.barometer.data.BarometerReading
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.stream.Collectors

/**
 * Created by jl on 3/18/18.
 */

class SensorReadFactory {

    fun getContract(context: Context): SensorReadContract {
        return SensorReadImpl(context)
    }

}

internal class SensorReadImpl(val context: Context) : SensorReadContract {

    override fun getPressureReading(nReadingsToAverage: Int): Observable<BarometerReading> {
        return ReactiveSensors(context)
                .observeSensor(Sensor.TYPE_PRESSURE)
                .observeOn(Schedulers.computation())
                .filter(ReactiveSensorFilter.filterSensorChanged())
                .map { it.sensorEvent.values[0] }
                .buffer(nReadingsToAverage)
                .map { it.stream().collect(Collectors.averagingDouble { x -> x.toDouble() }) }
                .map { BarometerReading(it) }
                .toObservable()
    }

}

