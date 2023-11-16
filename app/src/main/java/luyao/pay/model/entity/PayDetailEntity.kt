package luyao.pay.model.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass
import com.xkzhangsan.time.calculator.DateTimeCalculatorUtil
import kotlinx.parcelize.Parcelize
import java.util.Date

/**
 * Description: 单次的订阅详情
 * Author: luyao
 * Date: 2023/8/28 10:25
 */
@Parcelize
@JsonClass(generateAdapter = true)
@Entity(tableName = "pay_detail")
data class PayDetailEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,     // 唯一标识
    @ColumnInfo(name = "pay_id")
    var payId: String, // 所属订阅
    @ColumnInfo(name = "price")
    var price: String,  // 价格
    @ColumnInfo(name = "repeat")
    var repeatMode: RepeatMode,    // 订阅周期
    @ColumnInfo(name = "start_time")
    var startTime: Long, // 开始时间
    @ColumnInfo(name = "remark")
    var remark: String = "", // 备注
) : Parcelable {

    /**
     * 获取订阅年份
     */
    fun getStartYear() = DateTimeCalculatorUtil.getYear(Date(startTime))

    /**
     * 获取订阅月份
     */
    fun getStartMonth() = DateTimeCalculatorUtil.getMonth(Date(startTime))

    /**
     * 获取订阅日期
     */
    fun getStartDayOfMonth() = DateTimeCalculatorUtil.getDayOfMonth(Date(startTime))


    /**
     * 获取已订阅天数
     */
    fun getPayDays(): Long {
        var lastDate = getLastDate()
        if (lastDate > Date()) {
            lastDate = Date()
        }
        val days = DateTimeCalculatorUtil.betweenTotalDays(Date(startTime), lastDate)
        return if (days < 0) 0 else days
    }

    /**
     * 获取到期日
     */
    fun getLastDate() = when (repeatMode.freq) {
        FreqType.Daily -> {
            DateTimeCalculatorUtil.plusDays(Date(startTime), repeatMode.interval.toLong())
        }

        FreqType.Weekly -> {
            DateTimeCalculatorUtil.plusWeeks(Date(startTime), repeatMode.interval.toLong())
        }

        FreqType.Monthly -> {
            DateTimeCalculatorUtil.plusMonths(Date(startTime), repeatMode.interval.toLong())
        }

        FreqType.Quarterly -> {
            DateTimeCalculatorUtil.plusMonths(Date(startTime), (repeatMode.interval * 3).toLong())
        }

        FreqType.Yearly -> {
            DateTimeCalculatorUtil.plusYears(Date(startTime), repeatMode.interval.toLong())
        }

        else -> {
            Date(startTime)
        }
    }

    /**
     * 获取还有多少天过期
     */
    fun getExpireDays(): Long {
        return DateTimeCalculatorUtil.betweenTotalDays(Date(), getLastDate())
    }

    fun isExpire() = getExpireDays() <= 0

}
