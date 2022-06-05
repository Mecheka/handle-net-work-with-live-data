package com.example.handlenet

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.telephony.TelephonyManager
import android.util.Log
import androidx.lifecycle.LiveData

class HandleNetworkLiveData : LiveData<MyNetworkState>() {

    private lateinit var _context: Context
    private lateinit var networkRequest: NetworkRequest

    override fun onActive() {
        super.onActive()
        getDetail()
    }

    fun init(context: Context) {
        _context = context
        networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()
    }

    private fun getDetail() {
        val connectivityManager =
            _context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.requestNetwork(
            networkRequest,
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    val isCellular =
                        connectivityManager.getNetworkCapabilities(network)?.hasTransport(
                            NetworkCapabilities.TRANSPORT_CELLULAR
                        )!!
                    if (isCellular) {
                        getMccMnc()
                    } else {
                        postValue(MyNetworkState(isConnect = true, isCellular = isCellular))
                    }
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    postValue(MyNetworkState(isConnect = false, isCellular = false))
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    postValue(MyNetworkState(isConnect = false, isCellular = false))
                }
            })
    }

    private fun getMccMnc() {
        val telephonyManager =
            _context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        Log.d("Log", "Something")
        postValue(
            MyNetworkState(
                isConnect = true, isCellular = true,
                carrierId = telephonyManager.networkOperator,
                carrierName = telephonyManager.networkOperatorName
            )
        )
    }
}