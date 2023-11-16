package luyao.pay.base

import android.graphics.Color
import android.os.Bundle
import androidx.core.view.WindowCompat
import luyao.ktx.base.BaseActivity

/**
 * Description:
 * Author: luyao
 * Date: 2023/8/19 00:27
 */
abstract class BasePayActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
    }


}