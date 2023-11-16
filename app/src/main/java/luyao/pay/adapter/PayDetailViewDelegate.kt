package luyao.pay.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewDelegate
import luyao.ktx.ext.background
import luyao.ktx.util.toFormatString
import luyao.pay.PayApp
import luyao.pay.R
import luyao.pay.databinding.ItemPayDetailBinding
import luyao.pay.model.entity.PayDetailEntity
import luyao.pay.util.CurrencyUtil
import java.text.NumberFormat

/**
 * Description:
 * Author: luyao
 * Date: 2023/9/1 17:03
 */
class PayDetailViewDelegate(private val itemClick: (PayDetailEntity) -> Unit) :
    ItemViewDelegate<PayDetailEntity, PayDetailViewDelegate.ViewHolder>() {

    override fun onBindViewHolder(holder: ViewHolder, item: PayDetailEntity) {
        holder.binding.run {
            startDateTv.text = item.startTime.toFormatString("yyyy-MM-dd")
            endDateTv.text = item.getLastDate().time.toFormatString("yyyy-MM-dd")
            periodTv.text = item.repeatMode.toDisplayText()
            priceTv.text = CurrencyUtil.formatCurrency(item.price.toFloat())

            startDateTv.setTextColor(PayApp.App.getColor(if (item.isExpire()) luyao.ktx.R.color.color_999999 else R.color.white))
            endDateTv.setTextColor(PayApp.App.getColor(if (item.isExpire()) luyao.ktx.R.color.color_999999 else R.color.white))
            periodTv.setTextColor(PayApp.App.getColor(if (item.isExpire()) luyao.ktx.R.color.color_999999 else R.color.white))
            priceTv.setTextColor(PayApp.App.getColor(if (item.isExpire()) luyao.ktx.R.color.color_999999 else R.color.white))
            root.background(
                PayApp.App.getColor(if (item.isExpire()) R.color.white else R.color.color_58d047),
                8
            )

            root.setOnClickListener {
                itemClick.invoke(item)
            }
        }
    }

    override fun onCreateViewHolder(context: Context, parent: ViewGroup) = ViewHolder(
        ItemPayDetailBinding.inflate(LayoutInflater.from(context), parent, false)
    )

    class ViewHolder(val binding: ItemPayDetailBinding) : RecyclerView.ViewHolder(binding.root)
}