package luyao.pay.model.entity

import com.squareup.moshi.JsonClass

/**
 * Description:
 * Author: luyao
 * Date: 2023/9/11 15:53
 */
@JsonClass(generateAdapter = true)
data class BackupEntity(
    val appVersion: String,
    val time: Long,
    val payList: List<PayEntity>,
    val payDetailList: List<PayDetailEntity>
)