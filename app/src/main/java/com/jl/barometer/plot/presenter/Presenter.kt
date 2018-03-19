package com.jl.barometer.plot.presenter

import com.jl.barometer.data.BarometerReading
import com.jl.barometer.plot.IPlot
import com.jl.barometer.sensor.BarometerSensorListener
import io.reactivex.Observable

/**
 * Created by jl on 1/18/18.
 */
class Presenter(val sensorListener: BarometerSensorListener) : IPlot.Presenter {

    override fun getData(): Observable<BarometerReading> {
        return Observable.fromIterable(sensorListener.readings)
    }

    override fun getDataPoint(): BarometerReading {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}