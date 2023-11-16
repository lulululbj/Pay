package luyao.pay.view.dialog

import android.content.Context
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.loper7.date_time_picker.DateTimeConfig
import luyao.pay.databinding.DialogChooseDateBinding
import luyao.pay.databinding.DialogRepeatModeBinding
import luyao.pay.model.entity.FreqDisplayMap
import luyao.pay.model.entity.RepeatMode

/**
 * Description:
 * Author: luyao
 * Date: 2022/4/27 11:03
 */
class DatePickerDialog(context: Context) : BottomSheetDialog(context) {

    private val binding: DialogChooseDateBinding = DialogChooseDateBinding.inflate(layoutInflater)

    private var onDateChoose: (Long) -> Unit = {}

    fun setOnDateChoose(listener: (Long) -> Unit) {
        this.onDateChoose = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        binding.run {

            datePicker.run {
                setDisplayType(intArrayOf(
                    DateTimeConfig.YEAR,
                    DateTimeConfig.MONTH,
                    DateTimeConfig.DAY
                ))
                setGlobal(DateTimeConfig.GLOBAL_LOCAL)
//                setOnDateTimeChangedListener { millisecond ->
//                    onDateChoose.invoke(millisecond)
//                }
            }


            dialogCancel.setOnClickListener { dismiss() }
            dialogSubmit.setOnClickListener {
                onDateChoose.invoke(datePicker.getMillisecond())
                dismiss()
            }
        }
    }

    fun setDefaultTime(time: Long) {
        binding.datePicker.setDefaultMillisecond(time)
    }
}