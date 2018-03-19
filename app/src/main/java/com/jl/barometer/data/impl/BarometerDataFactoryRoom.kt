package com.jl.barometer.data.impl

import android.arch.persistence.room.*
import android.content.Context
import com.jl.barometer.data.BarometerDataContract
import com.jl.barometer.data.BarometerReading
import io.reactivex.Flowable
import io.reactivex.annotations.NonNull

class BarometerDataFactoryRoom {

    fun getContract(context: Context): BarometerDataContract {
        return BarometerDatabaseHelper2(context, BarometerDatabaseConst.DATABASE_NAME)
    }

    fun getTestContract(context: Context, dbName: String) : BarometerDataContract {
        return BarometerDatabaseHelper2(context, dbName)
    }

}


internal class BarometerDatabaseHelper2(context: Context, dbName : String) :
        BarometerDataContract {

    private val db = BarometerReadingsDatabase.getInstance(context, dbName)

    override fun addReading(reading: BarometerReading) {
        db.barometricReadingsDao().addReading(
                BarometerReadingDao(reading.reading, reading.time)
        )
    }

    override fun getAllData(): Flowable<List<BarometerReading>> {
        return db.barometricReadingsDao().getAllData()
                .flatMap { list ->
                    Flowable.fromIterable(list)
                            .map { item -> BarometerReading(item.pressure, item.timestamp) }
                            .toList()
                            .toFlowable()
                }
    }

    fun deleteAllData() {
        db.barometricReadingsDao().deleteAll()
    }

    fun deleteReadingDao(reading: BarometerReadingDao) {
        db.barometricReadingsDao().delete(reading)
    }

}


@Entity (tableName = BarometricReadingsTableConst.TABLE_NAME)
data class BarometerReadingDao(
    @ColumnInfo(name = BarometricReadingsTableConst.COLUMN_PRESSURE)
        var pressure: Double,
    @ColumnInfo(name = BarometricReadingsTableConst.COLUMN_TIMESTAMP)
        var timestamp: Long,
    @NonNull @PrimaryKey(autoGenerate = true)
        var id : Long = 0
) {
    constructor():this(0.0, 0)
}


@Dao
interface BarometricReadingsDao {

    @Query("SELECT * FROM " + BarometricReadingsTableConst.TABLE_NAME)
    fun getAllData(): Flowable<List<BarometerReadingDao>>

    @Insert
    fun addReading(reading: BarometerReadingDao)

    @Query("DELETE FROM " + BarometricReadingsTableConst.TABLE_NAME)
    fun deleteAll()

    @Delete
    fun delete(reading: BarometerReadingDao)
}


@Database(entities = [BarometerReadingDao::class],
        version = BarometerDatabaseConst.DATABASE_VERSION)
internal abstract class BarometerReadingsDatabase: RoomDatabase() {

    abstract fun barometricReadingsDao() : BarometricReadingsDao

    companion object {
        private var INSTANCE: BarometerReadingsDatabase? = null

        fun getInstance(context: Context, dbName: String):
                BarometerReadingsDatabase {
            if (INSTANCE == null) {
                synchronized(BarometerReadingsDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                            context.applicationContext,
                            BarometerReadingsDatabase::class.java,
                            dbName)
                            .build()
                }
            }
            return INSTANCE as BarometerReadingsDatabase
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}


internal class BarometerDatabaseConst {
    companion object {
        const val DATABASE_NAME = "barometerDatabase"
        const val DATABASE_VERSION = 1
    }
}
