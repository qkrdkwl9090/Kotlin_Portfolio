package com.dodong.whereismymask

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class StoreByAddressResponse(
    var address: String? = null,
    var count: Int? = null,
    var stores: List<Store>? = null
) : Serializable {
    class Store(
        var code: String? = null,
        var name: String? = null,
        var addr: String? = null,
        var type: String? = null,
        var lat: Double? = null,
        var lng: Double? = null,
        @SerializedName("stock_at") var stockAt: String? = null,
        @SerializedName("remain_stat") var remainStat: String? = null,
        @SerializedName("created_at") var createedAt: String? = null
    ) : Serializable {
        fun getstockAt(): String? {
            val stockat =stockAt ?: "알수없음"
            return stockAt
        }

        fun getremainStat(): String? {

            return remainStat
        }


        fun getname(): String? {
            return name
        }

        fun getaddr(): String? {
            return addr
        }

        fun getlat(): Double? {
            return lat
        }

        fun getlng(): Double? {
            return lng
        }
    }


}













