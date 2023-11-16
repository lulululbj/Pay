package luyao.pay.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import luyao.pay.model.entity.PayEntity
import luyao.pay.model.entity.PayWithDetail

/**
 * Description:
 * Author: luyao
 * Date: 2023/8/26 07:53
 */
@Dao
interface PayDao {

    @Query("SELECT * FROM pay")
    fun loadALl(): List<PayEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(record: PayEntity): Long

    @Update
    suspend fun update(record: PayEntity)

    @Transaction
    suspend fun insertOrUpdate(record: PayEntity) {
        if (insert(record) == -1L) {
            update(record)
        }
    }

    @Query("SELECT * FROM pay")
    fun loadAllPayWithDetail(): Flow<List<PayWithDetail>>

    @Query("SELECT * FROM pay where id = :id")
    suspend fun loadPayWithDetail(id: String): PayWithDetail

    @Query("delete from pay where id = :id")
    suspend fun delete(id: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBatch(payList: List<PayEntity>)
}