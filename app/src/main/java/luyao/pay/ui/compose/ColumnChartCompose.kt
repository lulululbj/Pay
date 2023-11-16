package luyao.pay.ui.compose

import android.graphics.Typeface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.component.shapeComponent
import com.patrykandpatrick.vico.compose.component.textComponent
import com.patrykandpatrick.vico.compose.dimensions.dimensionsOf
import com.patrykandpatrick.vico.compose.legend.horizontalLegend
import com.patrykandpatrick.vico.compose.legend.legendItem
import com.patrykandpatrick.vico.compose.style.ProvideChartStyle
import com.patrykandpatrick.vico.compose.style.currentChartStyle
import com.patrykandpatrick.vico.core.chart.column.ColumnChart
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.marker.Marker
import com.patrykandpatrick.vico.core.marker.MarkerVisibilityChangeListener
import luyao.pay.vm.StatisticVM

/**
 * Description:
 * Author: luyao
 * Date: 2023/9/19 14:52
 */

@Composable
fun ComposeColumnChart(staticVM: StatisticVM) {
    ProvideChartStyle(rememberChartStyle(columnChartColors)) {
        val legendNameList = staticVM.columnChartLegendNameData.observeAsState()
        val defaultColumns = currentChartStyle.columnChart.columns
        Chart(
            chart = columnChart(
                columns = remember(defaultColumns) {
                    defaultColumns.map { defaultColumn ->
                        LineComponent(defaultColumn.color, COLUMN_WIDTH_DP, defaultColumn.shape)
                    }
                },
                mergeMode = ColumnChart.MergeMode.Stack
            ),
            chartModelProducer = staticVM.columnChartEntryProducer,
            startAxis = rememberStartAxis(
                guideline = null,
                valueFormatter = IntAxisValueFormatter()
            ),
            bottomAxis = rememberBottomAxis(guideline = null),
            marker = rememberMarker(),
            legend = rememberLegend(legendNameList.value ?: arrayListOf("")),
            runInitialAnimation = false,
            markerVisibilityChangeListener = object : MarkerVisibilityChangeListener {
                override fun onMarkerShown(
                    marker: Marker,
                    markerEntryModels: List<Marker.EntryModel>
                ) {
                    super.onMarkerShown(marker, markerEntryModels)
                    staticVM.handleColumnChartClick(markerEntryModels[0].entry.x - 1)
                }
            }
        )
    }
}

@Composable
private fun rememberLegend(columnLegendName: List<String>) = horizontalLegend(
    items = columnLegendName.mapIndexed { index, legendName ->
        legendItem(
            icon = shapeComponent(
                Shapes.pillShape,
                columnChartColors[index % columnChartColors.size]
            ),
            label = textComponent(
                color = currentChartStyle.axis.axisLabelColor,
                textSize = legendItemLabelTextSize,
                typeface = Typeface.MONOSPACE,
            ),
            labelText = legendName
        )
    },
    iconSize = legendItemIconSize,
    iconPadding = legendItemIconPaddingValue,
    spacing = legendItemSpacing,
    padding = legendPadding,
)

private const val COLOR_1_CODE = 0xff916cda
private const val COLOR_2_CODE = 0xffd877d8
private const val COLOR_3_CODE = 0xfff094bb
private const val COLOR_4_CODE = 0xfffdc8c4

private val color1 = Color(COLOR_1_CODE)
private val color2 = Color(COLOR_2_CODE)
private val color3 = Color(COLOR_3_CODE)
private val color4 = Color(COLOR_4_CODE)
private val columnChartColors = listOf(color1, color2, color3, color4)
private const val COLUMN_WIDTH_DP = 12f
private val legendItemLabelTextSize = 12.sp
private val legendItemIconSize = 8.dp
private val legendItemIconPaddingValue = 6.dp
private val legendItemSpacing = 8.dp
private val legendTopPaddingValue = 8.dp
private val legendPadding = dimensionsOf(top = legendTopPaddingValue)