package luyao.pay.model.entity

import android.os.Parcelable
import androidx.annotation.ColorInt
import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import com.squareup.moshi.Json
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal
import kotlin.math.max
import kotlin.math.min

/**
 * Description:
 * Author: luyao
 * Date: 2023/9/6 20:21
 */
@Parcelize
data class PayWithDetail(
    @Embedded val payEntity: PayEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "pay_id"
    )
    val payDetailList: List<PayDetailEntity>
) : Parcelable {

    @IgnoredOnParcel
    @Ignore
    @ColorInt
    @Json(ignore = true)
    var colorInt: Int = 0

    /**
     * 获取已订阅的总天数
     */
    fun getPayDays(): Long {
        var days = 0L
        for (payDetail in payDetailList) {
            days += payDetail.getPayDays()
        }
        return days
    }

    /**
     * 获取还有多少天过期
     * 如果记录全部过期，才判定为过期
     * 天数取未过期记录中的最大值
     */
    fun getExpireDays(): Long {
        if (payDetailList.isEmpty()) return 0
        var noExpireDays = 0L
        var expireDays = 0L
        payDetailList.map { it.getExpireDays() }
            .forEach {
                if (it > 0) {
                    noExpireDays = max(noExpireDays, it)
                } else {
                    expireDays = min(expireDays, it)
                }
            }
        if (noExpireDays > 0) return noExpireDays
        return expireDays
    }

    fun getTotalPay(): BigDecimal {
        var total = BigDecimal(0)
        for (payDetail in payDetailList) {
            total = total.add(BigDecimal(payDetail.price))
        }
        return total
    }

    fun getTotalPaySpecificYear(filterYear: Int): BigDecimal {
        var totalPay = BigDecimal(0)
        payDetailList.forEach { detail ->
            val startYear = detail.getStartYear()
            if (filterYear == 0 || startYear == filterYear) {
                totalPay = totalPay.add(BigDecimal(detail.price))
            }
        }
        return totalPay
    }
}