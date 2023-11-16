package luyao.pay.ui.setting

import android.annotation.SuppressLint
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.hi.dhl.binding.viewbind
import dagger.hilt.android.AndroidEntryPoint
import luyao.ktx.ext.startActivity
import luyao.pay.R
import luyao.pay.base.BasePayFragment
import luyao.pay.databinding.FragmentSettingBinding
import luyao.pay.model.GlobalData
import luyao.pay.model.MMKVConstants
import luyao.pay.ui.AboutActivity
import luyao.pay.util.CurrencyUtil
import luyao.pay.view.dialog.CurrencyDialog

/**
 * Description:
 * Author: luyao
 * Date: 2023/8/19 00:37
 */
@AndroidEntryPoint
class SettingFragment : BasePayFragment(R.layout.fragment_setting) {

    private val binding: FragmentSettingBinding by viewbind()

    override fun initView() {
        initListener()
    }

    override fun initData() {

    }

    @SuppressLint("CheckResult")
    private fun initListener() {
        binding.run {

            toolbar.setOnMenuItemClickListener { item ->
                if (item.itemId == R.id.action_night_mode) {
                    MaterialDialog(requireActivity()).show {
                        listItemsSingleChoice(
                            R.array.night_mode,
                            initialSelection = getNightModeSelection()
                        ) { _, index, _ ->
                            saveNightMode(index)
                        }
                    }
                }
                true
            }

            // 默认货币
            defaultCurrency.settingClick = {
                CurrencyDialog(requireContext()).apply {
                    show()
                }
            }

            // 备份
            backup.settingClick = {
                startActivity<BackupActivity>()
            }

            // 导出
//            export.settingClick = { }

            // 关于
            about.settingClick = {
                startActivity<AboutActivity>()
            }

            // 给个好评
            jumpStore.settingClick = { }

            // 分享给好友
            share.settingClick = { }

//            // 隐私政策
//            secretLicense.settingClick = { }
//
//            // 用户协议
//            userLicense.settingClick = { }
        }
    }

    override fun startObserve() {
        super.startObserve()
        GlobalData.currencyData.observe(viewLifecycleOwner) {
            binding.defaultCurrency.setRightText(it.displayName)
        }
    }

    private fun getNightModeSelection() = when (MMKVConstants.nightMode) {
        AppCompatDelegate.MODE_NIGHT_NO -> 0
        AppCompatDelegate.MODE_NIGHT_YES -> 1
        AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> 2
        else -> 0
    }

    private fun saveNightMode(index: Int) {
        when (index) {
            0 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                MMKVConstants.nightMode = AppCompatDelegate.MODE_NIGHT_NO
            }

            1 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                MMKVConstants.nightMode = AppCompatDelegate.MODE_NIGHT_YES
            }

            2 -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                MMKVConstants.nightMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        }
    }
}