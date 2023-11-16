package luyao.pay.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.ItemViewDelegate
import luyao.pay.PayApp
import luyao.pay.R
import luyao.pay.databinding.ItemPayBinding
import luyao.pay.model.entity.PayWithDetail

/**
 * Description:
 * Author: luyao
 * Date: 2023/8/21 10:53
 */
class PayViewDelegate(
    private val clickPay: (PayWithDetail) -> Unit,
    private val longClickPay: (PayWithDetail, View) -> Unit
) : ItemViewDelegate<PayWithDetail, PayViewDelegate.ViewHolder>() {

    override fun onCreateViewHolder(context: Context, parent: ViewGroup) =
        ViewHolder(ItemPayBinding.inflate(LayoutInflater.from(context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, item: PayWithDetail) {
        holder.binding.run {
            payName.text = item.payEntity.name
            item.payEntity.setIconToView(payIcon)
            val expireDays = item.getExpireDays()
            expireDay.text = buildSpannedString {
                if (expireDays < 0) {
                    color(PayApp.App.getColor(R.color.color_red)) {
                        bold {
                            append(PayApp.App.getString(R.string.has_expired))
                        }
                    }
                } else if (expireDays == 0L) {
                    color(PayApp.App.getColor(R.color.color_red)) {
                        bold {
                            append(PayApp.App.getString(R.string.today_expire))
                        }
                    }
                } else {
                    color(PayApp.App.getColor(if (expireDays > 10) luyao.ktx.R.color.color_999999 else R.color.color_red)) {
                        bold {
                            append(expireDays.toString())
                        }
                    }
                    color(PayApp.App.getColor(luyao.ktx.R.color.color_999999)) {
                        append(PayApp.App.getString(R.string.expire_at))
                    }

                }

            }
            root.setOnClickListener {
                clickPay.invoke(item)
            }
            root.setOnLongClickListener { view ->
                longClickPay.invoke(item, view)
                true
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, item: PayWithDetail, payloads: List<Any>) {
        super.onBindViewHolder(holder, item, payloads)
    }

    class ViewHolder(val binding: ItemPayBinding) : RecyclerView.ViewHolder(binding.root)
}