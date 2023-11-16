package luyao.pay.ui

import android.content.Intent
import androidx.activity.viewModels
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.drakeet.multitype.MultiTypeAdapter
import com.hi.dhl.binding.viewbind
import dagger.hilt.android.AndroidEntryPoint
import luyao.ktx.util.MultiDiffCallback
import luyao.pay.R
import luyao.pay.adapter.SelectAppViewDelegate
import luyao.pay.base.BasePayActivity
import luyao.pay.databinding.ActivityChooseAppBinding
import luyao.pay.model.entity.AppEntity
import luyao.pay.vm.SelectAppVM

/**
 * Description:
 * Author: luyao
 * Date: 2022/10/8 22:19
 */
@AndroidEntryPoint
class ChooseAppActivity : BasePayActivity() {

    private val binding: ActivityChooseAppBinding by viewbind()
    private val vm by viewModels<SelectAppVM>()
    private val multiTypeAdapter = MultiTypeAdapter()
    private val selectViewDelegate = SelectAppViewDelegate(appClick = {
        handleSelectApp(it)
    },
        appLongClick = {})

    private fun handleSelectApp(it: AppEntity) {
        setResult(RESULT_OK, Intent().apply {
            putExtra("appEntity", it)
        })
        finish()
    }

    private val appList = arrayListOf<AppEntity>()

    override fun initView() {
        configRootInset(binding.root)
        configToolbar(binding.titleLayout.toolBar, getString(R.string.search_app))
        initRv()
        binding.swipeRefreshLayout.setOnRefreshListener {
            binding.searchEt.setText("")
        }
        binding.swipeRefreshLayout.isRefreshing = true
    }

    override fun initData() {
        vm.searchApp("")
        binding.searchEt.doAfterTextChanged {
            vm.searchApp(binding.searchEt.text.toString())
        }
    }

    private fun initRv() {
        selectViewDelegate.setSelectListener { appEntity, selected ->
            if (selected) handleSelectApp(appEntity)
        }
        binding.appRv.run {
            layoutManager = GridLayoutManager(this@ChooseAppActivity, 4)
            multiTypeAdapter.register(selectViewDelegate)
            multiTypeAdapter.items = appList
            adapter = multiTypeAdapter
        }
    }

    override fun observe() {
        super.observe()
        vm.appData.observe(this) {
            val callback = MultiDiffCallback(appList, it)
            val result = DiffUtil.calculateDiff(callback)
            appList.clear()
            appList.addAll(it)
            result.dispatchUpdatesTo(multiTypeAdapter)
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }
}