package com.jl.barometer.data.impl

import android.provider.BaseColumns

/**
 * Created by jl on 2/3/18.
 */
class BarometricReadingsTableConst {

    companion object {
        const val TABLE_NAME =  "BarometricReadings"
        val COLUMN_ID = BaseColumns._ID
        const val COLUMN_TIMESTAMP = "timestamp"
        const val COLUMN_PRESSURE = "pressure"
    }

}