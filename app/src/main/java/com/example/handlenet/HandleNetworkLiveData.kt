package com.example.handlenet

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiManager
import android.os.Build
import android.telephony.TelephonyManager
import android.util.Log
import androidx.lifecycle.LiveData

class HandleNetworkLiveData : LiveData<MyNetworkState>() {

    private lateinit var _context: Context
    private lateinit var networkRequest: NetworkRequest
    private var connectivityManager: ConnectivityManager? = null

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

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            val isCellular =
                connectivityManager?.getNetworkCapabilities(network)?.hasTransport(
                    NetworkCapabilities.TRANSPORT_CELLULAR
                )!!
            if (isCellular) {
                getMccMnc()
            } else {
                getWifiInfo()
            }
        }

        override fun onUnavailable() {
            super.onUnavailable()
            postValue(NotConnected)
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            postValue(NotConnected)
        }
    }

    private fun getDetail() {
        connectivityManager =
            _context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
            connectivityManager?.registerNetworkCallback(networkRequest, networkCallback)
        } else {
            connectivityManager?.requestNetwork(networkRequest, networkCallback)
        }
    }

    private fun getMccMnc() {
        val telephonyManager =
            _context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        Log.d("Log", "Something")
        postValue(
            Cellular(
                isConnect = true, isCellular = true,
                carrierId = telephonyManager.networkOperator,
                carrierName = telephonyManager.networkOperatorName
            )
        )
    }

    private fun getWifiInfo() {
        val wifi = _context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val info = wifi.connectionInfo
        postValue(
            Wifi(
                isConnect = true,
                isCellular = false,
                brand = info.bssid,
                operator = info.ssid
            )
        )
    }

    companion object {
        private const val TAG_WIFI_INFO = "Wifi info ==>"
    }
}