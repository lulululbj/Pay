package luyao.pay.model

import androidx.room.TypeConverter
import luyao.pay.model.entity.RepeatMode
import luyao.pay.util.MoshiUtil

/**
 * Description:
 * Author: luyao
 * Date: 2023/8/28 10:35
 */
class RoomConverters {

//    @TypeConverter
//    fun fromJsonToPayDetailList(json: String): MutableList<PayDetailEntity>? {
//        return MoshiUtil.payDetailListAdapter.fromJson(json)
//    }
//
//    @TypeConverter
//    fun payDetailListToJson(list: MutableList<PayDetailEntity>): String {
//        return MoshiUtil.payDetailListAdapter.toJson(list)
//    }

    @TypeConverter
    fun fromJsonToRepeatMode(json: String) = MoshiUtil.repeatModeAdapter.fromJson(json)

    @TypeConverter
    fun repeatModeToJson(repeatMode: RepeatMode) = MoshiUtil.repeatModeAdapter.toJson(repeatMode)

}