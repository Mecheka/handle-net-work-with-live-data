package com.example.handlenet

sealed class MyNetworkState

data class Wifi(
    val isConnect: Boolean,
    val isCellular: Boolean,
    val brand: String? = null,
    val operator: String? = null
) : MyNetworkState()

data class Cellular(
    val isConnect: Boolean,
    val isCellular: Boolean,
    val carrierId: String? = null,
    val carrierName: String? = null
) : MyNetworkState()

object NotConnected : MyNetworkState()