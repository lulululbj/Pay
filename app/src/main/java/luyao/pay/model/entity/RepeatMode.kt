package luyao.pay.model.entity

import android.os.Parcelable
import androidx.annotation.StringDef
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize
import luyao.pay.model.entity.FreqType.Companion.Daily
import luyao.pay.model.entity.FreqType.Companion.Monthly
import luyao.pay.model.entity.FreqType.Companion.Permanent
import luyao.pay.model.entity.FreqType.Companion.Quarterly
import luyao.pay.model.entity.FreqType.Companion.Weekly
import luyao.pay.model.entity.FreqType.Companion.Yearly

/**
 * Description:
 * Author: luyao
 * Date: 2022/4/30 15:49
 */

@Retention(AnnotationRetention.SOURCE)
@StringDef(Daily, Weekly, Monthly, Quarterly, Yearly, Permanent)
annotation class FreqType {
    companion object {
        const val Daily = "DAILY"
        const val Weekly = "WEEKLY"
        const val Monthly = "MONTHLY"
        const val Quarterly = "QUARTERLY"
        const val Yearly = "YEARLY"
        const val Permanent = "PERMANENT"
    }
}

val FreqDisplayMap = hashMapOf(
    Permanent to "永久",
    Yearly to "年",
    Quarterly to "季度",
    Monthly to "月",
    Weekly to "周",
    Daily to "日",
)

@Parcelize
@JsonClass(generateAdapter = true)
data class RepeatMode(
    @FreqType val freq: String,
    val interval: Int,
) : Parcelable {
    override fun toString(): String {
        return "FREQ=$freq;INTERVAL=$interval;WKST=SU"
    }

    fun toDisplayText(): String {
        return if (freq == Permanent) FreqDisplayMap[freq]
            ?: "" else "$interval ${FreqDisplayMap[freq]}"
    }
}
