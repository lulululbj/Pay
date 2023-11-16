package luyao.pay.ui.compose

import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.chart.values.ChartValues
import com.patrykandpatrick.vico.core.formatter.ValueFormatter

/**
 * Description:
 * Author: luyao
 * Date: 2023/9/20 10:31
 */
public open class IntValueFormatter() : ValueFormatter {

    override fun formatValue(value: Float, chartValues: ChartValues): CharSequence {
        return value.toInt().toString()
    }
}

public class IntAxisValueFormatter<Position : AxisPosition.Vertical>() :
    AxisValueFormatter<Position>, IntValueFormatter()