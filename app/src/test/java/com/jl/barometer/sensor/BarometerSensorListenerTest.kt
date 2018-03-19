package com.jl.barometer.sensor

import android.hardware.SensorEvent
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Created by jl on 1/13/18.
 */
class BarometerSensorListenerTest {

    @Test
    fun onSensorChanged() {
        val sensor = BarometerSensorListener()
        val event : SensorEvent? = null
        sensor.onSensorChanged(event)
        assertTrue(sensor.readings.isEmpty())
    }

}