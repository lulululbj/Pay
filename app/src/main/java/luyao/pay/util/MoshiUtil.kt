package luyao.pay.util

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import luyao.pay.model.entity.BackupEntity
import luyao.pay.model.entity.PayDetailEntity
import luyao.pay.model.entity.RepeatMode

/**
 * Description:
 * Author: luyao
 * Date: 2023/8/28 10:35
 */
object MoshiUtil {

    private val moshi = Moshi.Builder().build()

    val payDetailListAdapter: JsonAdapter<MutableList<PayDetailEntity>> = moshi.adapter(
        Types.newParameterizedType(
            MutableList::class.java,
            PayDetailEntity::class.java
        )
    )

    val repeatModeAdapter = moshi.adapter(RepeatMode::class.java)

    val backupEntityAdapter: JsonAdapter<BackupEntity> = moshi.adapter(BackupEntity::class.java)

}