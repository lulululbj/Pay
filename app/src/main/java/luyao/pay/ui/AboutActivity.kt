package luyao.pay.ui

import com.hi.dhl.binding.viewbind
import de.psdev.licensesdialog.LicensesDialog
import luyao.ktx.ext.copyToClipboard
import luyao.ktx.ext.sendEmail
import luyao.ktx.ext.toast
import luyao.ktx.ext.versionName
import luyao.pay.R
import luyao.pay.base.BasePayActivity
import luyao.pay.databinding.ActivityAboutBinding

/**
 * Description:
 * Author: luyao
 * Date: 2023/8/31 15:39
 */
class AboutActivity : BasePayActivity() {

    private val binding by viewbind<ActivityAboutBinding>()

    override fun initView() {
        configToolbarInset(binding.titleLayout.appBarLayout)
        configToolbar(binding.titleLayout.toolBar, getString(R.string.app_name))

        binding.run {
            versionTv.text = String.format("v%s", versionName)
        }

        initListener()
    }

    override fun initData() {

    }

    private fun initListener() {
        binding.run {
            aboutDeveloper.aboutClick = {
                copyToClipboard("bingxinshuo_")
                toast("已复制微信号")
            }

            aboutMail.aboutClick = {
                sendEmail("sunluyao1993x@gmail.com")
            }

            aboutQQ.aboutClick = {
            }

            userLicense.settingClick = {

            }

            secretLicense.settingClick = {

            }

            openLicense.settingClick = {
                LicensesDialog.Builder(this@AboutActivity)
                    .setNotices(R.raw.licenses)
                    .build()
                    .show()
            }
        }
    }
}