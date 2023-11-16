package luyao.pay.vm

import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import luyao.ktx.ext.versionName
import luyao.ktx.util.WebDavUtil
import luyao.ktx.util.YLog
import luyao.ktx.util.toFormatString
import luyao.pay.PayApp
import luyao.pay.R
import luyao.pay.model.MMKVConstants
import luyao.pay.model.dao.PayDao
import luyao.pay.model.dao.PayDetailDao
import luyao.pay.model.entity.BackupEntity
import luyao.pay.util.MoshiUtil
import net.lingala.zip4j.ZipFile
import java.io.File
import javax.inject.Inject

/**
 * Description:
 * Author: luyao
 * Date: 2023/6/9 10:28
 */
@HiltViewModel
class BackupVM @Inject constructor() : ViewModel() {

    @Inject
    lateinit var payDao: PayDao

    @Inject
    lateinit var payDetailDao: PayDetailDao

    val connectStatus = MutableLiveData<Boolean>()
    val progressLabel = MutableLiveData<String>()
    val backupResult = MutableLiveData<Result<String>>()
    val restoreResult = MutableLiveData<Result<BackupEntity>>()
    private val folderName = "Pay"
    private val localFileName = "local_backup.pay"

    fun checkWebDavConnect() {
        viewModelScope.launch(Dispatchers.IO) {
            connectStatus.postValue(
                WebDavUtil.checkWebDavSetting(
                    MMKVConstants.webDavServer,
                    MMKVConstants.webDavUserName,
                    MMKVConstants.webDavPassword
                )
            )
        }
    }

    fun backupLocal() {
        progressLabel.value = PayApp.App.getString(R.string.backing_to_local)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val backupEntity = getBackupEntity()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    // Android 10 以上使用 MediaStore API
                    val zipFile = zipBackFile(backupEntity, PayApp.App.cacheDir)
                    val values = ContentValues().apply {
                        put(MediaStore.Files.FileColumns.DISPLAY_NAME, zipFile.file.name)
                        put(
                            MediaStore.Files.FileColumns.RELATIVE_PATH,
                            "${Environment.DIRECTORY_DOWNLOADS}/${folderName}"
                        )
                    }
                    val uri = PayApp.App.contentResolver.insert(
                        MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                        values
                    )
                    uri?.let {
                        PayApp.App.contentResolver.openOutputStream(it).use { out ->
                            zipFile.file.inputStream().use { input ->
                                if (out != null) {
                                    input.copyTo(out)
                                }
                            }
                        }
                    }
                    zipFile.file.delete()
                } else {
                    // Android 10 以下使用 File API
                    val zipFolder = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        folderName
                    )
                    if (!zipFolder.exists())
                        zipFolder.mkdir()
                    zipBackFile(backupEntity, zipFolder)
                }
                MMKVConstants.lastBackupLocal = backupEntity.time
                backupResult.postValue(Result.success(PayApp.App.getString(R.string.backup_success)))
            } catch (e: Exception) {
                YLog.e(e.message ?: "")
                backupResult.postValue(Result.failure(Exception(PayApp.App.getString(R.string.backup_failed))))
            }
        }
    }

    fun restoreLocal(uri: Uri) {
        progressLabel.value = PayApp.App.getString(R.string.reading_local_backup_data)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val backUpFile = File(PayApp.App.cacheDir, "backup/backup.zip")
                if (!backUpFile.parentFile.exists()) backUpFile.mkdirs()
                if (backUpFile.exists()) backUpFile.delete()
                backUpFile.createNewFile()
                PayApp.App.contentResolver.openInputStream(uri)?.use { inputStream ->
                    backUpFile.outputStream().use {
                        inputStream.copyTo(it)
                    }
                }

                val zipFile = ZipFile(backUpFile)
                zipFile.extractAll("${PayApp.App.cacheDir.path}/backup/")

                val jsonFile = File(PayApp.App.cacheDir, "backup/local_backup.pay")
                if (!jsonFile.exists()) {
                    restoreResult.postValue(Result.failure(Exception(PayApp.App.getString(R.string.restore_failed))))
                } else {
                    val backupEntity =
                        MoshiUtil.backupEntityAdapter.fromJson(jsonFile.readText())
                    if (backupEntity == null) {
                        restoreResult.postValue(Result.failure(Exception(PayApp.App.getString(R.string.restore_failed))))
                    } else {
                        restoreResult.postValue(Result.success(backupEntity))
                    }
                }
                backUpFile.delete()
                jsonFile.delete()
            } catch (e: Exception) {
                restoreResult.postValue(Result.failure(Exception(PayApp.App.getString(R.string.restore_failed))))
                e.printStackTrace()
            }
        }
    }

    fun checkCloudData() {
        progressLabel.value = PayApp.App.getString(R.string.reading_cloud_backup_data)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val webDavFile = "${MMKVConstants.webDavServer}$folderName/backup.zip"

                val backUpFile = File(PayApp.App.cacheDir, "backup/backup.zip")
                if (!backUpFile.parentFile.exists()) backUpFile.mkdirs()
                if (backUpFile.exists()) backUpFile.delete()
                backUpFile.createNewFile()

                WebDavUtil.downLoad(webDavFile, backUpFile)
                val zipFile = ZipFile(backUpFile)
                zipFile.extractAll("${PayApp.App.cacheDir.path}/backup/")

                val jsonFile = File("${PayApp.App.cacheDir.path}/backup/", localFileName)
                if (jsonFile.exists()) {
                    val backupEntity =
                        MoshiUtil.backupEntityAdapter.fromJson(jsonFile.readText())
                    if (backupEntity == null) {
                        restoreResult.postValue(Result.failure(Exception(PayApp.App.getString(R.string.restore_failed))))
                    } else {
                        restoreResult.postValue(Result.success(backupEntity))
                    }
                    jsonFile.delete()
                } else {
                    restoreResult.postValue(Result.failure(Exception(PayApp.App.getString(R.string.restore_failed))))
                }
                backUpFile.delete()
            } catch (e: Exception) {
                YLog.e("restore cloud error: ${e.message}")
                restoreResult.postValue(Result.failure(Exception(PayApp.App.getString(R.string.restore_failed))))
            }
        }
    }

    fun backupCloud() {
        progressLabel.value = PayApp.App.getString(R.string.backing_up_to_cloud)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 备份实体类
                val backupEntity = getBackupEntity()
                // 生成本地压缩文件
                val zipFile = zipBackFile(backupEntity, PayApp.App.cacheDir)
                // 上传到云端
                WebDavUtil.upload(MMKVConstants.webDavServer, folderName, zipFile.file)
                MMKVConstants.lastBackupCloud = backupEntity.time
                backupResult.postValue(Result.success(PayApp.App.getString(R.string.backup_success)))
                // 备份成功，删除压缩文件
                zipFile.file.delete()
            } catch (e: Exception) {
                YLog.e(e.message ?: "")
                backupResult.postValue(Result.failure(Exception(PayApp.App.getString(R.string.backup_failed))))
            }
        }
    }

    fun restoreData(entity: BackupEntity) {
        progressLabel.value = PayApp.App.getString(R.string.restoring_cloud_backup_data)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                payDao.insertBatch(entity.payList)
                payDetailDao.insertBatch(entity.payDetailList)
                backupResult.postValue(Result.success(PayApp.App.getString(R.string.restore_success)))
            } catch (e: Exception) {
                backupResult.postValue(Result.failure(Exception(PayApp.App.getString(R.string.restore_failed))))
            }
        }
    }

    private fun getBackupEntity(): BackupEntity {
        return BackupEntity(
            PayApp.App.versionName,
            System.currentTimeMillis(),
            payDao.loadALl(),
            payDetailDao.loadALl()
        )
    }

    /*
     * 生成本地压缩文件
     */
    private fun zipBackFile(backupEntity: BackupEntity, folder: File): ZipFile {
        // json 数据写入本地文件
        val json = MoshiUtil.backupEntityAdapter.toJson(backupEntity)
        val jsonFile = File(folder, localFileName)
        if (jsonFile.exists()) jsonFile.delete()
        jsonFile.createNewFile()
        jsonFile.bufferedWriter().use { it.write(json) }

        // 生成压缩文件
        val zipFile = ZipFile(
            "${folder.absolutePath}/Pay_backup_${
                System.currentTimeMillis().toFormatString("yyyyMMdd_HHMMSS")
            }.zip"
        )
        zipFile.addFile(jsonFile)
        jsonFile.delete()
        return zipFile
    }
}