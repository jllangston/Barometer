package com.jl.barometer.plot

import com.jl.barometer.data.BarometerReading
import io.reactivex.Flowable
import io.reactivex.Observable


/**
 * Created by jl on 1/14/18.
 *
 * Plot interface
 */

interface IPlot {

    interface View {
        fun plotData(data: Flowable<BarometerReading>)
        fun plotData(data: Observable<BarometerReading>)
        fun plotData(data: List<BarometerReading>)
    }

    interface Presenter {

        fun getData(): Observable<BarometerReading>

        fun getDataPoint(): BarometerReading

    }

}