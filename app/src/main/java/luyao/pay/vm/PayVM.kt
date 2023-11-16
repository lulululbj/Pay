package luyao.pay.vm

import android.graphics.Bitmap
import android.util.Base64
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import luyao.ktx.base.BaseVM
import luyao.ktx.ext.toByteArray
import luyao.ktx.model.UiState
import luyao.pay.model.dao.PayDao
import luyao.pay.model.dao.PayDetailDao
import luyao.pay.model.entity.PayDetailEntity
import luyao.pay.model.entity.PayEntity
import luyao.pay.model.entity.PayWithDetail
import luyao.pay.model.entity.RepeatMode
import java.util.UUID
import javax.inject.Inject

/**
 * Description:
 * Author: luyao
 * Date: 2023/8/21 11:20
 */
@HiltViewModel
class PayVM @Inject constructor() : BaseVM() {

    @Inject
    lateinit var payDao: PayDao

    @Inject
    lateinit var payDetailDao: PayDetailDao

    val payWithDetailListData = MutableLiveData<List<PayWithDetail>>()
    val saveResult = MutableLiveData<UiState<Boolean>>()
    val payWithDetailData = MutableLiveData<PayWithDetail>()

    fun loadAllPay() {
        viewModelScope.launch {
            payDao.loadAllPayWithDetail().collect {
                payWithDetailListData.value = it
            }
        }
    }

    fun savePay(
        payEntity: PayEntity?,
        payDetailEntity: PayDetailEntity?,
        name: String,
        price: String,
        remark: String,
        bitmap: Bitmap?,
        repeatMode: RepeatMode?,
        startDateMillis: Long
    ) {

        handleBusiness(
            flow {
                if (name.isEmpty()) {
                    throw Exception("请输入订阅名称")
                }

                if (price.isEmpty()) {
                    throw Exception("请输入订阅价格")
                }

                if (repeatMode == null) {
                    throw Exception("请选择订阅周期")
                }

                if (startDateMillis == 0L) {
                    throw Exception("请选择开始日期")
                }

                if (payEntity == null) { // 无订阅源，新建订阅源和记录
                    val newPayEntity = PayEntity(
                        id = UUID.randomUUID().toString(),
                        icon = bitmap?.let {
                            Base64.encodeToString(
                                it.toByteArray(),
                                Base64.DEFAULT
                            )
                        }
                            ?: "",
                        name = name,
                    )
                    val newPayDetailEntity = PayDetailEntity(
                        id = UUID.randomUUID().toString(),
                        payId = newPayEntity.id,
                        price = price,
                        repeatMode = repeatMode,
                        startTime = startDateMillis,
                        remark = remark
                    )
                    payDao.insertOrUpdate(newPayEntity)
                    payDetailDao.insertOrUpdate(newPayDetailEntity)
                } else { // 有订阅源，新增或更新订阅记录
                    payEntity.run {
                        this.name = name
                        this.icon =
                            bitmap?.let { Base64.encodeToString(it.toByteArray(), Base64.DEFAULT) }
                                ?: this.icon
                    }
                    payDao.insertOrUpdate(payEntity)
                    if (payDetailEntity == null) {
                        val newPayDetailEntity = PayDetailEntity(
                            id = UUID.randomUUID().toString(),
                            payId = payEntity.id,
                            price = price,
                            repeatMode = repeatMode,
                            startTime = startDateMillis,
                            remark = remark
                        )
                        payDetailDao.insertOrUpdate(newPayDetailEntity)
                    } else {
                        payDetailEntity.run {
                            this.payId = payEntity.id
                            this.price = price
                            this.repeatMode = repeatMode
                            this.startTime = startDateMillis
                            this.remark = remark
                        }
                        payDetailDao.insertOrUpdate(payDetailEntity)
                    }
                }
                emit(true)
            },
            onSuccess = {
                saveResult.value = UiState.Success(true)
            }
        )
    }

    fun deletePay(payEntity: PayEntity) {
        handleBusiness<Any>(flow {
            payDao.delete(payEntity.id)
            payDetailDao.deleteByPayId(payEntity.id)
        })
    }

    fun loadPayWithDetail(id: String) {
        handleBusiness(flow {
            emit(payDao.loadPayWithDetail(id))
        }, onSuccess = {
            payWithDetailData.value = it
        })
    }

}

