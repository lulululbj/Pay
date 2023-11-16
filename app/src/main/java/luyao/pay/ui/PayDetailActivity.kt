package luyao.pay.ui

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.drakeet.multitype.MultiTypeAdapter
import com.hi.dhl.binding.viewbind
import dagger.hilt.android.AndroidEntryPoint
import luyao.ktx.ext.dp2px
import luyao.ktx.ext.itemPadding
import luyao.ktx.ext.systemUI.handleRootInset
import luyao.ktx.util.MultiDiffCallback
import luyao.pay.R
import luyao.pay.adapter.PayDetailViewDelegate
import luyao.pay.base.BasePayActivity
import luyao.pay.databinding.ActivityPayDetailBinding
import luyao.pay.model.entity.PayDetailEntity
import luyao.pay.model.entity.PayWithDetail
import luyao.pay.util.CurrencyUtil
import luyao.pay.vm.PayVM
import java.text.NumberFormat

/**
 * Description:
 * Author: luyao
 * Date: 2023/9/1 11:11
 */
@AndroidEntryPoint
class PayDetailActivity : BasePayActivity() {

    private val binding: ActivityPayDetailBinding by viewbind()
    private val payVM by viewModels<PayVM>()
    private lateinit var payWithDetail: PayWithDetail
    private val payDetailList = arrayListOf<PayDetailEntity>()
    private val multiTypeAdapter by lazy { MultiTypeAdapter() }
    private val payDetailViewDelegate by lazy {
        PayDetailViewDelegate { item ->
            payEditLauncher.launch(Intent(this, PayEditActivity::class.java).apply {
                putExtra("payEntity", payWithDetail.payEntity)
                putExtra("payDetailEntity", item)
                putExtra("canEdit", !item.isExpire())
            })
        }
    }
    private val payEditLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                // 刷新数据
                payVM.loadPayWithDetail(payWithDetail.payEntity.id)
            }
        }

    override fun initView() {
        bindVM(payVM)
        binding.root.handleRootInset()
        payWithDetail = intent.getParcelableExtra<PayWithDetail>("payWithDetail") as PayWithDetail
        configToolbar(binding.titleLayout.toolBar, payWithDetail.payEntity.name)
        setInfo()
        initRv()
    }

    override fun initData() {
        submitDiffList(payWithDetail.payDetailList)
    }

    private fun setInfo() {
        binding.run {
            payWithDetail.payEntity.setIconToView(payIcon)
            totalDaysTv.text = "%d天".format(payWithDetail.getPayDays())
            totalPayTv.text = CurrencyUtil.formatCurrency(payWithDetail.getTotalPay().toFloat())
        }
    }

    private fun initRv() {
        binding.run {
            payDetailRv.run {
                layoutManager = LinearLayoutManager(context)
                itemPadding(0, dp2px(8).toInt())
                adapter = multiTypeAdapter.apply {
                    register(payDetailViewDelegate)
                    items = payDetailList
                }
            }
        }
    }

    private fun submitDiffList(newList: List<PayDetailEntity>) {
        val callback = MultiDiffCallback(payDetailList, newList)
        val result = DiffUtil.calculateDiff(callback)
        payDetailList.clear()
        payDetailList.addAll(newList)
        result.dispatchUpdatesTo(multiTypeAdapter)
    }

    override fun observe() {
        super.observe()

        payVM.run {
            payWithDetailData.observe(this@PayDetailActivity) {
                payWithDetail = it
                setInfo()
                submitDiffList(it.payDetailList)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_add -> {
                payEditLauncher.launch(Intent(this, PayEditActivity::class.java).apply {
                    putExtra("payEntity", payWithDetail.payEntity)
                    putExtra("canEdit", true)
                })
            }
        }
        return super.onOptionsItemSelected(item)
    }
}