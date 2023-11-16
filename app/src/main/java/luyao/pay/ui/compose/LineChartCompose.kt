package luyao.pay.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.core.marker.Marker
import com.patrykandpatrick.vico.core.marker.MarkerVisibilityChangeListener
import luyao.pay.vm.StatisticVM

/**
 * Description:
 * Author: luyao
 * Date: 2023/9/19 14:27
 */

@Composable
fun ComposeLineChart(staticVM: StatisticVM = viewModel()) {
    val marker = rememberMarker()
    ProvideChartStyle(rememberChartStyle(chartColors)) {
        Chart(
            chart = lineChart(persistentMarkers = remember(marker) { mapOf(PERSISTENT_MARKER_X to marker) }),
            chartModelProducer = staticVM.lineChartEntryProducer,
            startAxis = rememberStartAxis(
                guideline = null,
                valueFormatter = IntAxisValueFormatter()
            ),
            bottomAxis = rememberBottomAxis(guideline = null),
            marker = marker,
            markerVisibilityChangeListener = object : MarkerVisibilityChangeListener {
                override fun onMarkerShown(
                    marker: Marker,
                    markerEntryModels: List<Marker.EntryModel>
                ) {
                    super.onMarkerShown(marker, markerEntryModels)
                    if (markerEntryModels[0].entry.y != 0f) {
                        staticVM.handleLineChartClick(markerEntryModels[0].entry.x)
                    }
                }
            },
            runInitialAnimation = false,
        )
    }
}

private const val COLOR_1_CODE = 0xffa485e0
private const val PERSISTENT_MARKER_X = 10f

private val color1 = Color(COLOR_1_CODE)
private val chartColors = listOf(color1)