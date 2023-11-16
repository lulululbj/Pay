package luyao.pay.view.dialog

import android.content.Context
import android.icu.util.Currency
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.drakeet.multitype.MultiTypeAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import luyao.ktx.ext.dp2px
import luyao.ktx.ext.getAppScreenHeight
import luyao.ktx.ext.itemPadding
import luyao.ktx.util.MultiDiffCallback
import luyao.pay.adapter.CurrencyViewDelegate
import luyao.pay.databinding.DialogCurrencyBinding
import luyao.pay.model.entity.CurrencyMode
import luyao.pay.util.CurrencyUtil

/**
 * Description:
 * Author: luyao
 * Date: 2023/9/21 17:26
 */
class CurrencyDialog(context: Context) : BottomSheetDialog(context) {

    private val binding = DialogCurrencyBinding.inflate(layoutInflater)
    private val multiTypeAdapter = MultiTypeAdapter()
    private val currencyList = arrayListOf<CurrencyMode>()
    private val currencyViewDelegate = CurrencyViewDelegate { currencyMode, position ->
        currencyList.forEach { it.selected = false }
        currencyMode.selected = true
        multiTypeAdapter.notifyItemRangeChanged(0, currencyList.size)
        dismiss()
    }

    init {
        setContentView(binding.root)
        initView()
        submitList(getCurrencyModeList())
    }

    private fun initView() {
        binding.run {
            currencyRv.run {
                setMaxHeight(context.getAppScreenHeight() / 2 - dp2px(64).toInt())
                itemPadding(verticalPadding = dp2px(8).toInt())
                layoutManager = WrapLinearLayoutManager(context)
                adapter = multiTypeAdapter.apply {
                    register(currencyViewDelegate)
                    items = currencyList
                }
            }

            searchEt.doAfterTextChanged {
                submitList(getCurrencyModeList(it.toString()))
            }
        }
    }

    private fun submitList(list: List<CurrencyMode>) {
        val callback = MultiDiffCallback(list, currencyList)
        val result = DiffUtil.calculateDiff(callback)
        currencyList.clear()
        currencyList.addAll(list)
        result.dispatchUpdatesTo(multiTypeAdapter)
    }

    private fun getCurrencyModeList(filterText: String = ""): List<CurrencyMode> {
        val currencyModeList = arrayListOf<CurrencyMode>()
        Currency.getAvailableCurrencies().forEach {
            if (it.displayName.contains(filterText) || it.symbol.contains(filterText)) {
                currencyModeList.add(CurrencyMode(it, it.currencyCode == CurrencyUtil.currencyCode))
            }
            currencyModeList.find { it.selected }?.let { currencyMode ->
                currencyModeList.remove(currencyMode)
                currencyModeList.add(0, currencyMode)
            }
        }
        return currencyModeList
    }

    class WrapLinearLayoutManager(
        context: Context?,

        ) : LinearLayoutManager(context) {

        override fun onLayoutChildren(
            recycler: RecyclerView.Recycler?,
            state: RecyclerView.State?
        ) {
            try {
                super.onLayoutChildren(recycler, state)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}