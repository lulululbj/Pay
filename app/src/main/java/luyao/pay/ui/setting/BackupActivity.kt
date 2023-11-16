package luyao.pay.ui.setting

import com.hi.dhl.binding.viewbind
import dagger.hilt.android.AndroidEntryPoint
import luyao.pay.R
import luyao.pay.base.BasePayActivity
import luyao.pay.databinding.ActivityBackupBinding

/**
 * Description:
 * Author: luyao
 * Date: 2023/9/11 15:31
 */
@AndroidEntryPoint
class BackupActivity : BasePayActivity() {

    private val binding by viewbind<ActivityBackupBinding>()

    override fun initView() {
        configToolbarInset(binding.titleLayout.appBarLayout)
        configToolbar(binding.titleLayout.toolBar, getString(R.string.data_backup))
    }

    override fun initData() {

    }
}