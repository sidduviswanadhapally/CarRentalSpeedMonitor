package com.example.carrentalspeedmonitor

import android.car.Car
import android.car.CarNotConnectedException
import android.car.VehiclePropertyIds
import android.car.hardware.CarPropertyValue
import android.car.hardware.property.CarPropertyManager
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    private lateinit var car: Car
    private lateinit var carPropertyManager: CarPropertyManager

    // Simulated renter ID and their configured speed limit
    private val renterId = "renter123"
    private val speedLimitMap = mapOf(
        "renter123" to 60,
        "renter456" to 70,
        "renter789" to 80
    )

    private val propID = VehiclePropertyIds.PERF_VEHICLE_SPEED
    private var lastSpeed: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initCar()
    }

    override fun onResume() {
        super.onResume()
        if (::car.isInitialized && !car.isConnected && !car.isConnecting) {
            car.connect()
        }
    }

    override fun onPause() {
        if (::car.isInitialized && car.isConnected) {
            car.disconnect()
        }
        super.onPause()
    }

    private fun initCar() {
        if (!packageManager.hasSystemFeature("android.hardware.type.automotive")) {
            Log.e(TAG, "FEATURE_AUTOMOTIVE not available")
            return
        }

        car = Car.createCar(this, object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                onCarServiceReady()
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                Log.e(TAG, "Car service disconnected")
            }
        })
    }

    private fun onCarServiceReady() {
        try {
            carPropertyManager = car.getCarManager(Car.PROPERTY_SERVICE) as CarPropertyManager
            registerSpeedListener()
            Log.d(TAG, "Car service connected, speed listener registered")
        } catch (e: CarNotConnectedException) {
            Log.e(TAG, "Car not connected", e)
        }
    }

    private fun registerSpeedListener() {
        carPropertyManager.registerCallback(object : CarPropertyManager.CarPropertyEventCallback {
            override fun onChangeEvent(value: CarPropertyValue<*>?) {
                if (value?.propertyId == propID && value.value is Float) {
                    val speed = (value.value as Float).toInt()

                    if (lastSpeed == null || lastSpeed != speed) {
                        Log.i(TAG, "Speed changed: $speed km/h")
                        lastSpeed = speed

                        val maxSpeed = speedLimitMap[renterId] ?: 60
                        if (speed > maxSpeed) {
                            notifyRentalCompany(renterId, speed, maxSpeed)
                            alertDriver(speed)
                        }
                    }
                }
            }

            override fun onErrorEvent(propertyId: Int, zone: Int) {
                Log.e(TAG, "Error reading vehicle property: $propertyId")
            }
        }, propID, CarPropertyManager.SENSOR_RATE_ONCHANGE)
    }

    private fun notifyRentalCompany(renterId: String, speed: Int, limit: Int) {
        Log.w(TAG, "Renter [$renterId] exceeded speed limit! ($speed > $limit)")
        // TODO: Send data to Firebase
        // sendToFirebase(renterId, speed, limit)

        // OR if using AWS:
        // sendToAWS(renterId, speed, limit)
    }

    private fun alertDriver(speed: Int) {
        Log.d(TAG, "ALERT DRIVER: You're going $speed km/h. Slow down!")
        // TODO: Replace with in-vehicle audio/visual alert or HMI integration
    }
}
