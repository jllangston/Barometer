package com.jl.barometer

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import com.amitshekhar.DebugDB
import com.github.mikephil.charting.charts.LineChart
import com.github.pwittchen.reactivesensors.library.ReactiveSensorFilter
import com.github.pwittchen.reactivesensors.library.ReactiveSensors
import com.jl.barometer.data.BarometerDataContract
import com.jl.barometer.data.BarometerReading
import com.jl.barometer.data.impl.BarometerDataFactoryRoom
import com.jl.barometer.plot.IPlot
import com.jl.barometer.plot.presenter.Presenter
import com.jl.barometer.plot.view.AndroidChart
import com.jl.barometer.rxsensor.SensorReadContract
import com.jl.barometer.rxsensor.SensorReadFactory
import com.jl.barometer.sensor.BarometerSensorListener
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.stream.Collectors


class MainActivity : Activity(), IPlot.View {
    lateinit var presenter: IPlot.Presenter
    lateinit var graphViewPlot : IPlot.View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initChart()

        //scheduleBarReadings(registerBarometer2())
        ScheduleReadPlot(this.applicationContext)
                .doPlot(graphViewPlot)

    }

    fun initChart() {
        val graph = findViewById<View>(R.id.graph) as LineChart
        val yAxis = graph.axisLeft
        yAxis.axisMaximum = 1024f
        yAxis.axisMinimum = 950f
        graphViewPlot = AndroidChart(graph)
    }


    fun registerBarometer() {
        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)
        val sensorListener = BarometerSensorListener()
        presenter = Presenter(sensorListener)
        sensorManager.registerListener(sensorListener, sensor, SensorManager.SENSOR_DELAY_NORMAL)

        Thread.sleep(1000)
        doPlot()
    }

    val nReadings = 25

    fun registerBarometer2() : Flowable<BarometerReading> {
        return ReactiveSensors(this.applicationContext).observeSensor(Sensor.TYPE_PRESSURE)
                .observeOn(Schedulers.computation())
                .filter(ReactiveSensorFilter.filterSensorChanged())
                .map { it.sensorEvent.values[0] }
                .buffer(nReadings)
                .map { it.stream().collect(Collectors.averagingDouble { x -> x.toDouble() }) }
                .map { BarometerReading(it) }
    }

    fun registerBarometer3() : BarometerDataContract {
       return BarometerDataFactoryRoom().getContract(this.applicationContext)
    }

    fun scheduleBarReadings(reading: Flowable<BarometerReading>) {
        plotData(reading.subscribeOn(Schedulers.io()))
    }

    fun doPlot() {
        plotData(presenter.getData())
    }

    override fun plotData(data: List<BarometerReading>) {
        graphViewPlot.plotData(data)
    }

    override fun plotData(data: Observable<BarometerReading>) {
        graphViewPlot.plotData(data)
    }

    override fun plotData(data: Flowable<BarometerReading>) {
        graphViewPlot.plotData(data)
    }

}

private class ScheduleReadPlot(val context: Context) {

    fun doPlot(graphViewPlot : IPlot.View) {
        DebugDB.getAddressLog()
        val dataContract = getDataContract()
        val sensorContract = getReadingContract()
        subscribePlot(dataContract, graphViewPlot)
        startSensorReadThread(sensorContract, dataContract)
    }

    private fun getDataContract() : BarometerDataContract {
        return BarometerDataFactoryRoom().getContract(context)
    }

    private fun getReadingContract() : SensorReadContract {
        return SensorReadFactory().getContract(context)
    }

    private fun subscribePlot(dataContract: BarometerDataContract,
                              graphViewPlot : IPlot.View) : Disposable {
        return dataContract.getAllData()
                .subscribeOn(Schedulers.io())
                .subscribe {
                    //it.forEach { println(it) }
                    graphViewPlot.plotData(it) }
    }

    private fun startSensorReadThread(sensorContract: SensorReadContract,
            dataContract: BarometerDataContract) {
        Thread(Runnable {
            while (true) {
                getSensorReadDisposable(sensorContract, dataContract)
                Thread.sleep(1000 * 5 * 1)
            }
        }).start()
    }

    private fun getSensorReadDisposable(sensorContract : SensorReadContract,
                                        dataContract: BarometerDataContract) {
        val data = sensorContract.getPressureReading()
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.io())
                .blockingFirst()
        println("Data: $data")
        dataContract.addReading(data)
    }





}


