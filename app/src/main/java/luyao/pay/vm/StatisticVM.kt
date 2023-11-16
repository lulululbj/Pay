package luyao.pay.vm

import android.icu.util.Currency
import androidx.lifecycle.MutableLiveData
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import luyao.ktx.base.BaseVM
import luyao.pay.PayApp
import luyao.pay.R
import luyao.pay.model.dao.PayDao
import luyao.pay.model.dao.PayDetailDao
import luyao.pay.model.entity.PayWithDetail
import luyao.pay.util.CurrencyUtil
import java.math.BigDecimal
import javax.inject.Inject

/**
 * Description:
 * Author: luyao
 * Date: 2023/9/13 14:40
 */
@HiltViewModel
class StatisticVM @Inject constructor() : BaseVM() {

    @Inject
    lateinit var payDao: PayDao

    @Inject
    lateinit var payDetailDao: PayDetailDao

    // 所有的数据
    private val payWithDetailList = arrayListOf<PayWithDetail>()

    // 当前 columnChart 的数据
    private val currentColumnChartPayWithDetailList = arrayListOf<PayWithDetail>()

    // columnChart 选中的数据
    private val selectedPayWithDetailData = MutableLiveData<PayWithDetail>()

    // legend 数据
    val columnChartLegendNameData = MutableLiveData<List<String>>()

    // 选中年份
    val selectedYear = MutableLiveData<String>()

    // 当前总订阅金额
    private var totalPayData = 0f
    val totalPayFormatData = MutableLiveData<String>()

    // lineChart 数据源
    val lineChartEntryProducer: ChartEntryModelProducer = ChartEntryModelProducer()

    // columnChart 数据源
    val columnChartEntryProducer: ChartEntryModelProducer = ChartEntryModelProducer()

    // columnChart 至少展示 7 条数据，不足的展示为 0
    private val columnCount = 7

    /**
     * 获取柱状图数据
     */
    fun generateColumnData(data: List<PayWithDetail>, filterYear: Int = 0) {
        if (data.isEmpty()) return
        handleBusiness(flow {
            val result = data.filter { it.getTotalPaySpecificYear(filterYear).toFloat() > 0 }
                .sortedByDescending { it.getTotalPaySpecificYear(filterYear) }
            val entryList = arrayListOf<List<FloatEntry>>()

            result.forEachIndexed { index, data ->
                entryList.add(
                    arrayListOf(
                        FloatEntry(
                            (index + 1).toFloat(),
                            data.getTotalPaySpecificYear(filterYear).toFloat()
                        )
                    )
                )
            }
            result.sumOf { it.getTotalPaySpecificYear(filterYear) }
                .let {
                    totalPayData = it.toFloat()
                    totalPayFormatData.postValue(CurrencyUtil.formatCurrency(totalPayData))
                }
            if (entryList.size < columnCount) {
                for (i in entryList.size until columnCount) {
                    entryList.add(arrayListOf(FloatEntry(i.toFloat(), 0f)))
                }
            }
            columnChartEntryProducer.setEntries(entryList)
            columnChartLegendNameData.postValue(result.map { item -> item.payEntity.name })
            emit(result)
        }, onSuccess = {
            currentColumnChartPayWithDetailList.clear()
            currentColumnChartPayWithDetailList.addAll(it)
        })
    }

    /**
     * 获取线性图数据
     */
    fun generateLineData(data: List<PayWithDetail>, filterYear: Int = 0) {
        if (data.isEmpty()) return
        handleBusiness(flow {
            val payMap = hashMapOf<Int, BigDecimal>()
            data.forEach {
                it.payDetailList.forEach { detail ->
                    val startYear = detail.getStartYear()
                    if (filterYear == 0 || startYear == filterYear) {
                        if (payMap.containsKey(startYear)) {
                            val originPrice = payMap[startYear]!!
                            val newPrice = originPrice.add(BigDecimal(detail.price))
                            payMap[startYear] = newPrice
                        } else {
                            payMap[startYear] = BigDecimal(detail.price)
                        }
                    }
                }
            }
            val entryList = arrayListOf<FloatEntry>()
            payMap.forEach { (i, bigDecimal) ->
                entryList.add(FloatEntry(i.toFloat(), bigDecimal.toFloat()))
            }
            if (entryList.size == 1) {
                entryList.add(0, FloatEntry(entryList[0].x - 1, 0f))
            }
            emit(entryList)
        }, onSuccess = {
            lineChartEntryProducer.setEntries(it)
        })
    }

    fun handleColumnChartClick(x: Float) {
        selectedPayWithDetailData.value = currentColumnChartPayWithDetailList[x.toInt()]
    }

    fun handleLineChartClick(x: Float) {
        generateColumnData(payWithDetailList, x.toInt())
        selectedYear.value = x.toInt().toString()
    }

    fun setAllPayWithDetails(data: List<PayWithDetail>) {
        payWithDetailList.clear()
        payWithDetailList.addAll(data)
    }

    fun restoreColumnChart() {
        selectedYear.value = PayApp.App.getString(R.string.all)
        generateColumnData(payWithDetailList, 0)
    }

    fun refreshPayCurrency(currency: Currency?) {
        currency?.let {
            totalPayFormatData.value = CurrencyUtil.formatCurrency(totalPayData)
        }
    }

}