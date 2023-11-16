package luyao.pay.view.dialog

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import luyao.pay.databinding.PopPayMenuBinding
import luyao.pay.model.entity.PayEntity

/**
 * Description: 记录长按弹出菜单
 * Author: luyao
 * Date: 2023/2/13 22:58
 */
class PayMenuPopWindow(val context: Context, val click: RecordLongClick) : PopupWindow(context) {

    private val binding: PopPayMenuBinding = PopPayMenuBinding.inflate(LayoutInflater.from(context))
    private lateinit var payEntity: PayEntity
    private var recordLongClick: RecordLongClick? = null
    private var popWindowWidth = 0
    private var popWindowHeight = 0
    private lateinit var view: View

    init {
        width = ViewGroup.LayoutParams.WRAP_CONTENT
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        contentView = binding.root
        this.setBackgroundDrawable(ColorDrawable(0))
        isOutsideTouchable = true

        binding.root.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        popWindowHeight = binding.root.measuredHeight
        popWindowWidth = binding.root.measuredWidth

        initListener()
    }

    private fun initListener() {
        binding.run {
            recordLongClick = click
            deleteLayout.setOnClickListener {
                recordLongClick?.delete(payEntity)
                dismiss()
            }

            shareLayout.setOnClickListener {
                recordLongClick?.share(payEntity)
                dismiss()
            }
        }
    }

    fun show(payEntity: PayEntity, view: View) {
        this.view = view
        this.payEntity = payEntity
        if (!isShowing) {
            val intArray = IntArray(2)
            view.getLocationOnScreen(intArray)
            showAtLocation(
                view,
                Gravity.NO_GRAVITY,
                intArray[0] + view.width / 2 - popWindowWidth / 2,
                intArray[1] - popWindowHeight
            )
        } else {
            dismiss()
        }
    }
}

interface RecordLongClick {
    fun delete(payEntity: PayEntity)
    fun share(payEntity: PayEntity)
}