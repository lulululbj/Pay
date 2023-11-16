package luyao.pay.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.hi.dhl.binding.viewbind
import dagger.hilt.android.AndroidEntryPoint
import luyao.pay.R
import luyao.pay.base.BasePayFragment
import luyao.pay.databinding.FragmentStatisticBinding
import luyao.pay.model.GlobalData
import luyao.pay.ui.compose.ComposeColumnChart
import luyao.pay.ui.compose.ComposeLineChart
import luyao.pay.vm.PayVM
import luyao.pay.vm.StatisticVM

/**
 * Description:
 * Author: luyao
 * Date: 2023/8/19 00:37
 */
@AndroidEntryPoint
class StatisticFragment : BasePayFragment(R.layout.fragment_statistic) {

    private val binding: FragmentStatisticBinding by viewbind()
    private val payVM by activityViewModels<PayVM>()
    private val statisticVM by viewModels<StatisticVM>()

    override fun lazyLoad() = false

    override fun initView() {
        initChart()
    }

    override fun initData() {

    }

    private fun initChart() {
        binding.composeView.setContent {
            val totalPayFormatState = statisticVM.totalPayFormatData.observeAsState()
            Column {
                Text(
                    getString(R.string.total_pay_format, totalPayFormatState.value ?: 0f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    modifier = Modifier.padding(start = 8.dp),
                    color = colorResource(id = R.color.color_total_pay_tv)
                )
                Spacer(modifier = Modifier.height(32.dp))
                Card(
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.elevatedCardColors()
                ) {
                    Column(Modifier.padding(16.dp)) {
                        FilledTonalButton(onClick = { statisticVM.restoreColumnChart() }) {
                            Text(
                                text = statisticVM.selectedYear.value
                                    ?: stringResource(id = R.string.all)
                            )
                        }
                        ComposeColumnChart(statisticVM)
                    }
                }
                Spacer(modifier = Modifier.height(48.dp))
                Card(
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.elevatedCardColors()
                ) {
                    Box(Modifier.padding(16.dp)) {
                        ComposeLineChart(statisticVM)
                    }
                }
            }
        }
    }

    override fun startObserve() {
        super.startObserve()
        payVM.run {
            payWithDetailListData.observe(viewLifecycleOwner) {
                if (it.isEmpty()) {
                    binding.composeView.isVisible = false
                } else {
                    binding.composeView.isVisible = true
                    statisticVM.setAllPayWithDetails(it)
                    statisticVM.generateLineData(it)
                    statisticVM.generateColumnData(it)
                }
            }
        }
        GlobalData.currencyData.observe(viewLifecycleOwner) {
            statisticVM.refreshPayCurrency(it)
        }
    }
}