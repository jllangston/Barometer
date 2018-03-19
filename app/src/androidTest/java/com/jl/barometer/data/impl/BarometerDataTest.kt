package com.jl.barometer.data.impl

import android.content.Context
import android.support.test.InstrumentationRegistry
import com.amitshekhar.DebugDB
import com.jl.barometer.data.BarometerDataContract
import com.jl.barometer.data.BarometerReading
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Created by jl on 2/3/18.
 */
//@RunWith(AndroidJUnit4::class)
class BarometerDataTest {

    val testDbName = "testBarometerDatabase"

    private lateinit var dataContract: BarometerDataContract

    @Before
    fun setup() {
        DebugDB.getAddressLog()
        val context: Context = InstrumentationRegistry.getTargetContext()
        dataContract = BarometerDataFactoryRoom().getTestContract(
                context, testDbName)
    }

    @After
    fun tearDown() {
        val dbHelper = dataContract as BarometerDatabaseHelper2
        dbHelper.deleteAllData()
    }

    @Test
    fun addReading() {
        val reading = BarometerReading(123.45)
        dataContract.addReading(reading)
        assertTrue(true)
    }

    @Test
    fun getAllData_pressure() {
        val reading = BarometerReading(123.45)
        dataContract.addReading(reading)

        val readings = dataContract.getAllData()
                .test()
                .awaitCount(1)
                .assertNoErrors()
                .events[0][0]

        val testReading = getReadingFromList(readings)
        assertEquals(123.45, testReading.reading, .01)
    }

    private fun getReadingFromList(any : Any) : BarometerReading {
        if (any is List<*>) {
            val r = any[0]
            if (r is BarometerReading) {
                return r
            }
        }
        throw IllegalArgumentException("Not a valid reading")
    }

    @Test
    fun getAllData_pressure_time() {
        val time = System.currentTimeMillis()
        val reading = BarometerReading(123.45, time)
        dataContract.addReading(reading)


        val returnedReading : BarometerReading
        val events = dataContract.getAllData()
                .test()
                .awaitCount(1)
                .assertNoErrors()
                .events[0][0]

        val testReading = getReadingFromList(events)
        assertEquals(testReading.time, time)
    }

}