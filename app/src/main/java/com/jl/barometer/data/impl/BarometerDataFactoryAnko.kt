package com.jl.barometer.data.impl

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.jl.barometer.data.BarometerDataContract
import com.jl.barometer.data.BarometerReading
import io.reactivex.Flowable
import io.reactivex.Observable
import org.jetbrains.anko.db.*

/**
 * Created by jl on 2/3/18.
 */
class BarometerDataFactory {

    fun getContract(context: Context): BarometerDataContract {
        return BarometerData(BarometerDatabaseHelper(context, BarometerDatabase.DATABASE_NAME))
    }
}


internal class BarometerData(val dbHelper: BarometerDatabaseHelper) : BarometerDataContract {

    override fun addReading(reading: BarometerReading) {
        dbHelper.use {
            insert(BarometricReadingsTableConst.TABLE_NAME,
                    BarometricReadingsTableConst.COLUMN_TIMESTAMP to reading.time,
                    BarometricReadingsTableConst.COLUMN_PRESSURE to reading.reading)
        }
    }

    override fun getAllData(): Flowable<List<BarometerReading>> {
        val readings: List<BarometerReading> = dbHelper.use {
            select(BarometricReadingsTableConst.TABLE_NAME)
                    .orderBy(BarometricReadingsTableConst.COLUMN_TIMESTAMP)
                    .exec { parseList(classParser()) }
        }
        return Observable.fromArray(readings).toFlowable(null)
    }

}


class BarometerDatabaseHelper(context: Context, dbName : String) :
        ManagedSQLiteOpenHelper(context,
                dbName,
                null,
                BarometerDatabase.DATABASE_VERSION) {

    companion object {
        private var instance: BarometerDatabaseHelper? = null

        @Synchronized
        fun getInstance(context: Context, dbName: String): BarometerDatabaseHelper {
            if (instance == null) {
                instance = BarometerDatabaseHelper(context.applicationContext, dbName)
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.createTable(BarometricReadingsTableConst.TABLE_NAME, true,
                BarometricReadingsTableConst.COLUMN_ID to INTEGER + PRIMARY_KEY,
                BarometricReadingsTableConst.COLUMN_PRESSURE to REAL,
                BarometricReadingsTableConst.COLUMN_TIMESTAMP to INTEGER
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }


}


private class BarometerDatabase {
    companion object {
        val DATABASE_NAME = "barometerDatabase"
        val DATABASE_VERSION = 1
    }
}
