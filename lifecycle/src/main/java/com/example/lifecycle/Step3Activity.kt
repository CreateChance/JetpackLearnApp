package com.example.lifecycle

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleService

class Step3Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_step3)
    }

    fun startLBS(view: View) {
        startService(Intent(this, MyLocationService::class.java))
    }

    fun stopLBS(view: View) {
        stopService(Intent(this, MyLocationService::class.java))
    }
}

class MyLocationService : LifecycleService() {
    init {
        // service 创建的时候添加生命周期监听器
        val locationObserver = MyLocationObserver(this)
        lifecycle.addObserver(locationObserver)
        Log.d("GAOCHAO", "MyLocationService 初始化完毕!")
    }
}

class MyLocationObserver(val context: Context) : DefaultLifecycleObserver {

    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: MyLocationListener

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        startGetLocation()
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        stopGetLocation()
    }

    private fun startGetLocation() {
        Log.d("GAOCHAO", "startGetLocation")
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = MyLocationListener()
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            3000L,
            1.0f,
            locationListener
        )
    }

    private fun stopGetLocation() {
        Log.d("GAOCHAO", "stopGetLocation")
        locationManager.removeUpdates(locationListener)
    }
}

class MyLocationListener : LocationListener {
    override fun onLocationChanged(location: Location) {
        Log.d("GAOCHAO", "onLocationChanged: $location")
    }
}

