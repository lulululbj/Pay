package luyao.pay.model.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Description:
 * Author: luyao
 * Date: 2023/8/28 13:56
 */
@Parcelize
data class AppEntity(
    val name: String,
    val packageName: String,
) : Parcelable
