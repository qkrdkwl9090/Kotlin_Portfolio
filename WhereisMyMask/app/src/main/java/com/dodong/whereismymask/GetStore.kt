package com.dodong.whereismymask

import java.io.Serializable
class Store(
    val code: String? = null,
    val name: String? = null,
    val addr: String? = null,
    val type: String? = null,
    val lat: Number? = null,
    val lng: Number? = null
) : Serializable