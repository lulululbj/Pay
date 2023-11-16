package luyao.pay.model

import androidx.appcompat.app.AppCompatDelegate
import luyao.ktx.util.MMKVDelegate

/**
 * Description:
 * Author: luyao
 * Date: 2023/9/11 15:35
 */

const val SECRET_LICENSE_URL = "https://www.yuque.com/bingxinshuotm/fkhvug/id1p8k"
const val USER_LICENSE_URL = "https://www.yuque.com/bingxinshuotm/fkhvug/olwz5b"
const val JIANGUO_WEBDAV_URL = "https://dav.jianguoyun.com/dav/"
const val WEBDAV_HELP_URL = "https://content.jianguoyun.com/3991.html"
const val FEEDBACK_URL = "https://support.qq.com/product/425842?d-wx-push=1"

object MMKVConstants {
    var webDavUserName by MMKVDelegate("webdav_username", "")
    var webDavPassword by MMKVDelegate("webdav_password", "")
    var webDavServer by MMKVDelegate("webdav_server", JIANGUO_WEBDAV_URL)
    var lastBackupCloud by MMKVDelegate("last_backup_cloud", 0L)
    var lastBackupLocal by MMKVDelegate("last_backup_local", 0L)
    var currencyCode by MMKVDelegate("currency_code", "")
    var nightMode by MMKVDelegate("night_mode", AppCompatDelegate.MODE_NIGHT_NO) // 夜间模式

}