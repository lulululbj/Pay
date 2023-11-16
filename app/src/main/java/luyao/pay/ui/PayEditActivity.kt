package luyao.pay.ui

import android.content.Intent
import android.graphics.Bitmap
import android.view.Menu
import android.view.MenuItem
import android.widget.PopupMenu
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.graphics.drawable.toBitmap
import androidx.core.widget.doAfterTextChanged
import com.hi.dhl.binding.viewbind
import dagger.hilt.android.AndroidEntryPoint
import luyao.ktx.ext.dp2px
import luyao.ktx.ext.showConfirmDialog
import luyao.ktx.ext.systemUI.handleRootInset
import luyao.ktx.ext.systemUI.immersiveNavigationBar
import luyao.ktx.ext.systemUI.setLightNavigationBar
import luyao.ktx.ext.toBitmap
import luyao.ktx.model.UiState
import luyao.ktx.util.getCircleTextBitmap
import luyao.ktx.util.toFormatString
import luyao.pay.PayApp
import luyao.pay.R
import luyao.pay.base.BasePayActivity
import luyao.pay.databinding.ActivityPayEditBinding
import luyao.pay.model.entity.AppEntity
import luyao.pay.model.entity.PayDetailEntity
import luyao.pay.model.entity.PayEntity
import luyao.pay.util.CurrencyUtil
import luyao.pay.view.dialog.DatePickerDialog
import luyao.pay.view.dialog.PeriodDialog
import luyao.pay.vm.PayVM

/**
 * Description:
 * Author: luyao
 * Date: 2023/8/21 17:02
 */
@AndroidEntryPoint
class PayEditActivity : BasePayActivity() {

    private val binding: ActivityPayEditBinding by viewbind()
    private val payVM by viewModels<PayVM>()
    private val payEntity by lazy { intent.getParcelableExtra<PayEntity?>("payEntity") }
    private val payDetailEntity by lazy { intent.getParcelableExtra<PayDetailEntity?>("payDetailEntity") }
    private val canEdit by lazy { intent.getBooleanExtra("canEdit", true) }
    private var bitmap: Bitmap? = null
    private val bitmapSize = dp2px(48).toInt()
    private val periodDialog by lazy { PeriodDialog(this) }
    private var cardDatePickerDialog: DatePickerDialog? = null
    private var menu: Menu? = null
    private var contentChanged = false
    private var startDateMillis = 0L

    private val photoPickerLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let {
                binding.payIcon.setImageURI(it)
                it.toBitmap(application)
                    ?.let { originBitmap ->
                        bitmap = Bitmap.createScaledBitmap(
                            originBitmap, bitmapSize, bitmapSize, true
                        )
                    }
                contentChanged()
            }
        }

    private val chooseAppLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result?.data?.getParcelableExtra<AppEntity>("appEntity")?.let {
                    chooseAppEntity(it)
                    contentChanged()
                }
            }
        }

    override fun initView() {
        bindVM(payVM)
        binding.root.handleRootInset()
        configToolbar(binding.titleLayout.toolBar, payEntity?.name ?: getString(R.string.add_pay))
        initInfo()
        initListener()
    }

    private fun initInfo() {
        binding.run {
            priceInput.hint = CurrencyUtil.formatCurrency(0.0f)
        }

        if (payEntity == null) return

        binding.run {
            startDateMillis = payDetailEntity?.startTime ?: 0L
            nameInput.setText(payEntity?.name)
            priceInput.setText(payDetailEntity?.price)
            remarkInput.setText(payDetailEntity?.remark)
            payEntity?.setIconToView(payIcon)
            periodInput.text = payDetailEntity?.repeatMode?.toDisplayText()
            dateInput.text = payDetailEntity?.startTime?.toFormatString("yyyy-MM-dd")
            payDetailEntity?.repeatMode?.let { periodDialog.setRepeatMode(it) }
            if (payEntity?.icon.isNullOrEmpty()) {
                payIcon.setImageBitmap(
                    getCircleTextBitmap(
                        payEntity?.name?.get(0).toString(),
                        bitmapSize
                    )
                )
            }
        }

    }

    private fun initListener() {
        binding.run {
            nameInput.isEnabled = canEdit
            priceInput.isEnabled = canEdit
            remarkInput.isEnabled = canEdit

            nameInput.doAfterTextChanged {
                if (bitmap == null && payEntity?.icon.isNullOrEmpty()) {
                    if (nameInput.text.toString().isNotEmpty()) {
                        val textBitmap =
                            getCircleTextBitmap(nameInput.text.toString()[0].toString(), bitmapSize)
                        payIcon.setImageBitmap(textBitmap)
                    } else {
                        payIcon.setImageResource(R.drawable.luyao)
                    }
                }
                contentChanged()
            }
            priceInput.doAfterTextChanged { contentChanged() }
            remarkInput.doAfterTextChanged { contentChanged() }

            // 选择图标
            payIcon.setOnClickListener {
                if (!canEdit) return@setOnClickListener
                PopupMenu(this@PayEditActivity, it).apply {
                    inflate(R.menu.menu_pay_icon)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.action_from_album -> {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                                true
                            }

                            R.id.action_from_apps -> {
                                chooseAppLauncher.launch(
                                    Intent(
                                        this@PayEditActivity,
                                        ChooseAppActivity::class.java
                                    )
                                )
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }

            // 订阅周期
            periodCard.setOnClickListener {
                if (!canEdit) return@setOnClickListener
                periodDialog.setOnSelectListener { mode ->
                    periodInput.text = mode.toDisplayText()
                    contentChanged()
                }
                periodDialog.run {
                    immersiveNavigationBar()
                    setLightNavigationBar(true)
                    show()
                }
            }

            // 开始日期
            dateCard.setOnClickListener {
                if (!canEdit) return@setOnClickListener
                if (cardDatePickerDialog == null)
                    cardDatePickerDialog = DatePickerDialog(this@PayEditActivity).apply {
                        setDefaultTime(if (startDateMillis == 0L) System.currentTimeMillis() else startDateMillis)
                        setOnDateChoose { millisecond ->
                            startDateMillis = millisecond
                            dateInput.text = millisecond.toFormatString("yyyy-MM-dd")
                            contentChanged()
                        }
                    }
                cardDatePickerDialog?.run {
                    immersiveNavigationBar()
                    setLightNavigationBar(true)
                    show()
                }
            }
        }
    }

    override fun initData() {
    }

    private fun chooseAppEntity(appEntity: AppEntity) {
        binding.run {
            val drawable = PayApp.App.packageManager.getPackageInfo(
                appEntity.packageName,
                0
            ).applicationInfo.loadIcon(
                PayApp.App.packageManager
            )
            payIcon.setImageDrawable(drawable)
            bitmap = drawable.toBitmap(bitmapSize, bitmapSize)
            nameInput.setText(appEntity.name)
        }
    }

    private fun contentChanged() {
        if (!canEdit) return
        contentChanged = true
        menu?.findItem(R.id.action_update_menu)?.isVisible = true
    }

    override fun handleBack() {
        if (contentChanged) {
            showConfirmDialog(
                message = if (payEntity == null) getString(R.string.wheather_to_save_this_pay) else getString(
                    R.string.content_changed_wheather_to_save
                ),
                onCancel = {
                    super.handleBack()
                }) {
                savePay()
            }
        } else {
            super.handleBack()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_update, menu)
        this.menu = menu
        menu?.findItem(R.id.action_update_menu)?.isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_update_menu -> {
                savePay()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun savePay() {
        val name = binding.nameInput.text.toString()
        val price = binding.priceInput.text.toString()
        val remark = binding.remarkInput.text.toString()

        payVM.savePay(
            payEntity,
            payDetailEntity,
            name,
            price,
            remark,
            bitmap,
            periodDialog.getRepeatMode(),
            startDateMillis
        )
    }

    override fun observe() {
        super.observe()

        payVM.run {
            saveResult.observe(this@PayEditActivity) {
                if (it is UiState.Success) {
                    contentChanged = false
                    menu?.findItem(R.id.action_update_menu)?.isVisible = false
                    setResult(RESULT_OK)
                    handleBack()
                }
            }
        }
    }

}