package luyao.pay.ui

import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.drakeet.multitype.MultiTypeAdapter
import com.hi.dhl.binding.viewbind
import dagger.hilt.android.AndroidEntryPoint
import luyao.ktx.ext.showConfirmDialog
import luyao.ktx.ext.startActivity
import luyao.ktx.util.MultiDiffCallback
import luyao.pay.R
import luyao.pay.adapter.PayViewDelegate
import luyao.pay.base.BasePayFragment
import luyao.pay.databinding.FragmentPayListBinding
import luyao.pay.model.entity.PayEntity
import luyao.pay.model.entity.PayWithDetail
import luyao.pay.view.dialog.PayMenuPopWindow
import luyao.pay.view.dialog.RecordLongClick
import luyao.pay.vm.PayVM

/**
 * Description: 首页订阅列表
 * Author: luyao
 * Date: 2023/8/19 00:37
 */
@AndroidEntryPoint
class PayListFragment : BasePayFragment(R.layout.fragment_pay_list) {

    private val binding: FragmentPayListBinding by viewbind()
    private val payVM by activityViewModels<PayVM>()
    private val multiTypeAdapter = MultiTypeAdapter()
    private val payViewDelegate = PayViewDelegate(
        clickPay = { startActivity<PayDetailActivity>("payWithDetail" to it) },
        longClickPay = { payWithDetail, view ->
            payMenuPop.show(payWithDetail.payEntity, view)
        }
    )
    private val payList = arrayListOf<PayWithDetail>()
    private val payMenuPop by lazy {
        PayMenuPopWindow(requireContext(), object : RecordLongClick {
            override fun delete(payEntity: PayEntity) {
                val note =
                    getString(R.string.whether_to_confirm_the_deletion_of_this_record).format(
                        payEntity.name
                    )
                showConfirmDialog(message = note) {
                    payVM.deletePay(payEntity)
                }
            }

            override fun share(payEntity: PayEntity) {

            }
        })
    }

    override fun initView() {
        binding.root.setOnClickListener { }
        initRv()
    }

    override fun initData() {
    }

    private fun initRv() {
        binding.payRv.run {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = multiTypeAdapter.apply {
                register(payViewDelegate)
                items = payList
            }
        }
    }

    override fun startObserve() {
        super.startObserve()

        payVM.run {
            payWithDetailListData.observe(viewLifecycleOwner) {
                submitDiffList(it)
            }
        }
    }

    private fun submitDiffList(newList: List<PayWithDetail>) {
        val callback = MultiDiffCallback(payList, newList)
        val result = DiffUtil.calculateDiff(callback)
        payList.clear()
        payList.addAll(newList)
        result.dispatchUpdatesTo(multiTypeAdapter)
    }
}