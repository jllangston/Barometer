package com.jl.barometer.service

import android.app.IntentService
import android.content.Intent
import com.jl.barometer.data.impl.BarometerDataFactoryRoom
import com.jl.barometer.rxsensor.SensorReadFactory
import io.reactivex.schedulers.Schedulers

/**
 * Created by jl on 3/24/18.
 *
 * Simple service to write sensor readings to db
 */

class GetReadingService : IntentService(GetReadingService::class.toString()) {

    override fun onHandleIntent(intent: Intent?) {
        val context = this.applicationContext
        val dataContract = BarometerDataFactoryRoom().getContract(context)
        val sensorContract = SensorReadFactory().getContract(context)

        val data = sensorContract.getPressureReading()
                .subscribeOn(Schedulers.computation())
                .blockingFirst()
        dataContract.addReading(data)
    }

}