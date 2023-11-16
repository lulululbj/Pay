package luyao.pay.model

import android.icu.util.Currency
import androidx.lifecycle.MutableLiveData
import luyao.pay.util.CurrencyUtil

/**
 * Description:
 * Author: luyao
 * Date: 2023/9/22 13:12
 */
object GlobalData {
    val currencyData = MutableLiveData(Currency.getInstance(CurrencyUtil.currencyCode))
}