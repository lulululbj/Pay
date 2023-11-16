package luyao.pay.util

import android.icu.text.NumberFormat
import android.icu.util.Currency
import luyao.pay.model.MMKVConstants
import java.util.Locale

/**
 * Description:
 * Author: luyao
 * Date: 2023/9/1 15:57
 */
object CurrencyUtil {

     val currencyCode: String
        get() = MMKVConstants.currencyCode.ifEmpty {
            Currency.getInstance(Locale.getDefault()).currencyCode
        }

    fun formatCurrency(price: Float): String =
        formatCurrency(price, currencyCode)

    private fun formatCurrency(price: Float, currency: String): String {
        val format = NumberFormat.getCurrencyInstance().apply {
            this.currency = Currency.getInstance(currency)
        }
        return format.format(price)
    }


    fun formatCurrency(amount: Float, currency: String, language: String, country: String): String =
        currencyInLocale(currency, language, country).format(amount)

    private fun currencyInLocale(
        currencyCode: String,
        language: String,
        country: String = "",
        variant: String = ""
    ): NumberFormat =
        Locale(language, country, variant).let {
            NumberFormat.getCurrencyInstance(it).apply {
                currency = Currency.getInstance(currencyCode)
            }
        }
}

