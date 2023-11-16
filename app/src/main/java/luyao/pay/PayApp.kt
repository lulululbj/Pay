package luyao.pay

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp
import luyao.ktx.app.AppInit
import luyao.pay.model.MMKVConstants

/**
 * Description:
 * Author: luyao
 * Date: 2023/8/19 00:26
 */
@HiltAndroidApp
class PayApp : Application() {

    companion object {
        lateinit var App: Application
    }

    override fun onCreate() {
        super.onCreate()
        App = this
        AppInit.init(this)
        AppCompatDelegate.setDefaultNightMode(MMKVConstants.nightMode)
    }
}