package luyao.pay.vm

import android.content.pm.LauncherApps
import android.os.Process
import androidx.core.content.getSystemService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import luyao.pay.PayApp
import luyao.pay.model.entity.AppEntity
import javax.inject.Inject

/**
 * Description:
 * Author: luyao
 * Date: 2022/10/9 17:20
 */
@HiltViewModel
class SelectAppVM @Inject constructor() : ViewModel() {

    private val appList = arrayListOf<AppEntity>()
    val appData: MutableLiveData<List<AppEntity>> = MutableLiveData()

    fun searchApp(keyword: String) {
        viewModelScope.launch(Dispatchers.IO) {

            if (appList.isNotEmpty()) {
                appData.postValue(appList.filter {
                    it.name.contains(
                        keyword,
                        true
                    ) || it.packageName.contains(keyword, true)
                })
                return@launch
            }

            appList.clear()

            PayApp.App.getSystemService<LauncherApps>()?.run {
                val packageManager = PayApp.App.packageManager
                val activityList = getActivityList(null, Process.myUserHandle())
                for (activityInfo in activityList) {
                    val appName = activityInfo.applicationInfo.loadLabel(packageManager).toString()
                    val packageName = activityInfo.applicationInfo.packageName
                    if (appName.contains(keyword, true) || packageName.contains(keyword, true)) {
                        appList.add(AppEntity(appName, packageName))
                    }
                }
                appData.postValue(appList)
            }
        }
    }
}