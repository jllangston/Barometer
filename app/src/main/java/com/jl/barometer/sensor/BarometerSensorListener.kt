package com.jl.barometer.sensor

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import com.jl.barometer.data.BarometerReading
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.schedulers.Schedulers

/**
 * Created by jl on 1/13/18.
 */

class BarometerSensorListener() : SensorEventListener, BarometerObservable {

    val readings : MutableList<BarometerReading> = mutableListOf()

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // not yet
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) {
            return
        }
        val mbValue = event.values[0]
        val reading = BarometerReading(mbValue)
        val sReading = reading.toString()
        System.out.println(sReading)
        readings.add(reading)
    }

    override fun getObservable(): Observable<BarometerReading> {
        return Observable.create(ObservableOnSubscribe {  })
    }

}

interface BarometerObservable {

    fun getObservable() : Observable<BarometerReading>

}



