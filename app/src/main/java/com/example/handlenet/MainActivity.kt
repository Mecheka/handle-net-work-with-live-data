package com.example.handlenet

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.handlenet.databinding.ActivityMainBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    if (p0?.areAllPermissionsGranted() == true) {
                        handleInternetState()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    p1?.continuePermissionRequest()
                }

            })
            .withErrorListener {
                Log.d("error", it.name)
            }
            .onSameThread()
            .check()

    }

    @SuppressLint("SetTextI18n")
    private fun handleInternetState() {
        val netLiveData = HandleNetworkLiveData()
        netLiveData.init(this@MainActivity)
        netLiveData.observe(this@MainActivity) {
            when (it) {
                is Cellular -> {
                    binding.netStatus.text = "Is internet connect ${it.isConnect}"
                    binding.isCellular.text = "Is connect cellular ${it.isCellular}"
                    binding.carrierId.text = "Carrier id ${it.carrierId.orEmpty()}"
                    binding.carrierName.text = "Carrier name ${it.carrierName.orEmpty()}"
                }
                is Wifi -> {
                    binding.netStatus.text = "Is internet connect ${it.isConnect}"
                    binding.isCellular.text = "Is connect cellular ${it.isCellular}"
                    binding.carrierId.text = "Brand ${it.brand.orEmpty()}"
                    binding.carrierName.text = "Operator ${it.operator.orEmpty()}"
                }
                NotConnected -> {
                    binding.netStatus.text = "Is internet connect false"
                    binding.isCellular.text = "Is connect cellular false"
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}