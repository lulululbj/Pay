package luyao.pay.model.entity

import android.icu.util.Currency

/**
 * Description:
 * Author: luyao
 * Date: 2023/9/22 09:52
 */
data class CurrencyMode(
    val currency: Currency,
    var selected: Boolean = false
)