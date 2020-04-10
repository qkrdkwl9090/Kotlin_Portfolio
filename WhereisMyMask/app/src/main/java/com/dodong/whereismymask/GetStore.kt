package com.dodong.whereismymask

import java.io.Serializable

class StoreByAddressResponse(
    var address: String? = null,
    var count: Int? = null
//    var stores: List<Store>? = null

) : Serializable
//{
//    inner class Store(
//        var code: String? = null,
//        var name: String? = null,
//        var addr: String? = null,
//        var type: String? = null,
//        var lat: Number? = null,
//        var lng: Number? = null,
//        var stockAt: String? = null,
//        var remainStat: String? = null,
//        var createedAt: String? = null
//    ) : Serializable
//
//}




