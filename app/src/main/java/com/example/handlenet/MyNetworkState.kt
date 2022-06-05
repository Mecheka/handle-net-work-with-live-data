package com.example.handlenet

data class MyNetworkState(
    val isConnect: Boolean,
    val isCellular: Boolean,
    val carrierId: String? = null,
    val carrierName: String? = null
)
