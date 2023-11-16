package luyao.pay.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import luyao.pay.model.dao.PayDao
import luyao.pay.model.dao.PayDetailDao
import luyao.pay.model.entity.PayDetailEntity
import luyao.pay.model.entity.PayEntity

/**
 * Description:
 * Author: luyao
 * Date: 2023/8/26 07:52
 */
@Database(
    entities = [PayEntity::class, PayDetailEntity::class],
    version = 1
)
@TypeConverters(RoomConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun payDao(): PayDao

    abstract fun payDetailDao(): PayDetailDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, AppDatabase::class.java, "pay").build()
    }
}