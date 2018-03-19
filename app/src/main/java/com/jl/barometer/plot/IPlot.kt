package com.jl.barometer.plot

import com.jl.barometer.data.BarometerReading
import io.reactivex.Flowable
import io.reactivex.Observable


/**
 * Created by jl on 1/14/18.
 */

interface IPlot {

    interface View {
        fun plotData(data: Flowable<BarometerReading>)
        fun plotData(data: Observable<BarometerReading>)
    }

    interface Presenter {

        fun getData(): Observable<BarometerReading>

        fun getDataPoint(): BarometerReading

    }

}