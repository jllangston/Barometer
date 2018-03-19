package com.jl.barometer.plot.view

import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.jl.barometer.data.BarometerReading
import com.jl.barometer.plot.IPlot
import io.reactivex.Flowable
import io.reactivex.Observable
import java.sql.Timestamp
import java.util.*

/**
 * Created by jl on 1/28/18.
 */
class AndroidChart(private val lineChart: LineChart): IPlot.View {

    val entries = ArrayList<Entry>()

    override fun plotData(data: Flowable<BarometerReading>) {
        data.subscribe { doPlot(it) }
    }

    override fun plotData(data: Observable<BarometerReading>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun doPlot(reading: BarometerReading) {
        val x = tsFormat(reading.time)
        entries.add(Entry(x, reading.reading.toFloat()))
        val lineDataSet = LineDataSet(entries, "Pressure [mb]")
        val lineData = LineData(lineDataSet)
        lineChart.data = lineData
        lineChart.invalidate()
    }

    private fun tsFormat(ts : Long): Float {
        val cal = Calendar.getInstance()
        cal.timeInMillis = ts
        return (
                //cal.get(Calendar.HOUR)*60*60 +
                cal.get(Calendar.MINUTE)*60 +
                cal.get(Calendar.SECOND)
                ).toFloat()
    }

}