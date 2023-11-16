package luyao.pay.adapter

import android.content.Context
import android.icu.util.Currency
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewDelegate
import luyao.ktx.util.YLog
import luyao.pay.R
import luyao.pay.databinding.ItemCurrencyBinding
import luyao.pay.model.GlobalData
import luyao.pay.model.MMKVConstants
import luyao.pay.model.entity.CurrencyMode

/**
 * Description:
 * Author: luyao
 * Date: 2023/9/21 17:30
 */
class CurrencyViewDelegate(private val currencyChoose: (CurrencyMode, Int) -> Unit) :
    ItemViewDelegate<CurrencyMode, CurrencyViewDelegate.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, item: CurrencyMode) {
        holder.binding.run {
            currencyTv.setBackground(if (item.selected) R.drawable.bg_single_setting_reverse else luyao.ktx.R.drawable.bg_single_setting)
            currencyTv.setSettingText("${item.currency.displayName}(${item.currency.symbol})")
            currencyTv.settingClick = {
                MMKVConstants.currencyCode = item.currency.currencyCode
                currencyChoose.invoke(item, holder.absoluteAdapterPosition)
                GlobalData.currencyData.value = item.currency
            }
        }
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup) =
        ViewHolder(ItemCurrencyBinding.inflate(LayoutInflater.from(context), parent, false))

    class ViewHolder(val binding: ItemCurrencyBinding) : RecyclerView.ViewHolder(binding.root)
}