package luyao.pay.ui

import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.navigation.NavigationBarView
import com.hi.dhl.binding.viewbind
import dagger.hilt.android.AndroidEntryPoint
import luyao.ktx.ext.alphaIn
import luyao.ktx.ext.alphaOut
import luyao.ktx.ext.startActivity
import luyao.ktx.ext.systemUI.handleRootInset
import luyao.pay.R
import luyao.pay.base.BasePayActivity
import luyao.pay.databinding.ActivityMainBinding
import luyao.pay.ui.setting.SettingFragment
import luyao.pay.vm.PayVM

/**
 * Description:
 * Author: luyao
 * Date: 2023/8/19 00:28
 */
@AndroidEntryPoint
class MainActivity : BasePayActivity() {

    private val binding: ActivityMainBinding by viewbind()
    private val payVM: PayVM by viewModels()
    private val payListFragment by lazy { PayListFragment() }
    private val statisticFragment by lazy { StatisticFragment() }
    private val settingFragment by lazy { SettingFragment() }
    override fun initView() {
        binding.root.handleRootInset()
        initTab()
    }

    override fun initData() {
        payVM.loadAllPay()
    }

    private fun initTab() {

        val fragmentList =
            arrayListOf<Fragment>(
                payListFragment,
                statisticFragment,
                settingFragment
            )

        binding.run {
            viewPager.isUserInputEnabled = false
            viewPager.offscreenPageLimit = fragmentList.size
            viewPager.adapter = object : FragmentStateAdapter(this@MainActivity) {
                override fun getItemCount() = fragmentList.size
                override fun createFragment(position: Int) = fragmentList[position]
            }

            viewPager.registerOnPageChangeCallback(pageChangeListener)
            bottomNav.labelVisibilityMode = NavigationBarView.LABEL_VISIBILITY_LABELED
            bottomNav.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.action_nav_record -> {
                        viewPager.setCurrentItem(0, false)
                    }

                    R.id.action_nav_statistic -> {
                        viewPager.setCurrentItem(1, false)
                    }

                    R.id.action_nav_settings -> {
                        viewPager.setCurrentItem(2, false)
                    }
                }
                true
            }

            fab.setOnClickListener { startActivity<PayEditActivity>() }
        }
    }

    private val pageChangeListener = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            binding.run {
                bottomNav.menu.getItem(position).isChecked = true
                if ((position == 0) && !fab.isVisible) {
                    fab.alphaIn()
                } else if (fab.isVisible) {
                    fab.alphaOut()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.viewPager.unregisterOnPageChangeCallback(pageChangeListener)
    }

    fun getAvailableContentHeight(): Int {
        return binding.root.height - binding.bottomNav.height
    }
}