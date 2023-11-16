package luyao.pay.model.entity

import android.os.Parcelable
import android.util.Base64
import android.widget.ImageView
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bumptech.glide.Glide
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
 * Description:
 * Author: luyao
 * Date: 2023/8/21 10:51
 */
@Parcelize
@JsonClass(generateAdapter = true)
@Entity(tableName = "pay")
data class PayEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,     // 唯一标识
    @ColumnInfo(name = "icon")
    var icon: String,   // 图标, Base64
    @ColumnInfo(name = "name")
    var name: String,   // 名称
//    @ColumnInfo(name = "pay_details")
//    val payList: MutableList<PayDetailEntity> = arrayListOf(), // 订阅详情
) : Parcelable {

    fun setIconToView(view: ImageView) {
        Glide.with(view).load(Base64.decode(icon, Base64.DEFAULT)).into(view)
    }
}

