package luyao.pay.view.dialog

import android.content.Context
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialog
import luyao.pay.databinding.DialogRepeatModeBinding
import luyao.pay.model.entity.FreqDisplayMap
import luyao.pay.model.entity.RepeatMode

/**
 * Description:
 * Author: luyao
 * Date: 2022/4/27 11:03
 */
class PeriodDialog(context: Context) : BottomSheetDialog(context) {

    private val binding: DialogRepeatModeBinding = DialogRepeatModeBinding.inflate(layoutInflater)
    private var onSelectListener: ((RepeatMode) -> Unit)? = null
    private var repeatMode: RepeatMode? = null
    fun setOnSelectListener(listener: ((RepeatMode) -> Unit)?) {
        this.onSelectListener = listener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        binding.run {

            val displayArray = FreqDisplayMap.values.toTypedArray()
            periodPicker.run {
                maxValue = displayArray.size
                minValue = 1
                displayedValues = displayArray
                isFadingEdgeEnabled = true
                setOnValueChangedListener { _, _, newVal ->
                    numberPicker.isScrollerEnabled = newVal != FreqDisplayMap.size
                }
            }

            dialogCancel.setOnClickListener { dismiss() }
            dialogSubmit.setOnClickListener {
                val freqType = FreqDisplayMap.keys.toMutableList()[periodPicker.value - 1]
                val interval = numberPicker.value
                val mode = RepeatMode(freqType, interval)
                repeatMode = mode
                onSelectListener?.invoke(mode)
                dismiss()
            }
        }
    }

    fun getRepeatMode(): RepeatMode? {
        return repeatMode
    }

    fun setRepeatMode(repeatMode: RepeatMode) {
        this.repeatMode = repeatMode
        binding.numberPicker.value = repeatMode.interval
        binding.periodPicker.value = FreqDisplayMap.keys.indexOf(repeatMode.freq) + 1
    }
}