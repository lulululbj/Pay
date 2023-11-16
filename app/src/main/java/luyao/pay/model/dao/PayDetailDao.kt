package luyao.pay.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import luyao.pay.model.entity.PayDetailEntity

/**
 * Description:
 * Author: luyao
 * Date: 2023/9/8 09:55
 */
@Dao
interface PayDetailDao {

    @Query("SELECT * FROM pay_detail")
    fun loadALl(): List<PayDetailEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(record: PayDetailEntity): Long

    @Update
    suspend fun update(record: PayDetailEntity)

    @Transaction
    suspend fun insertOrUpdate(record: PayDetailEntity) {
        if (insert(record) == -1L) {
            update(record)
        }
    }

    @Query("delete from pay_detail where pay_id = :payId")
    suspend fun deleteByPayId(payId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBatch(payList: List<PayDetailEntity>)
}