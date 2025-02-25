package com.example.tasklist.ui.fragment

import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.tasklist.R


class BrujulaFragment : Fragment(), SensorEventListener {

    // Views
    private lateinit var brujula: ImageView


    // Sensors
    private lateinit var sensorManager: SensorManager

    private var sensorMagnetico: Sensor? = null

    private var acelerometro: Sensor? = null

    private var gravedad: FloatArray? = null
    private var geomagnetico: FloatArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sensorManager = requireContext().getSystemService(SENSOR_SERVICE) as SensorManager
        setViews(view)
        setSensors()
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this,sensorMagnetico,SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this,acelerometro,SensorManager.SENSOR_DELAY_NORMAL)
    }

    private fun setViews(view: View) {
        brujula = view.findViewById(R.id.brujula)
    }

    private fun setSensors() {
        sensorMagnetico = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }


    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_brujula, container, false)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if(event != null){
            when(event.sensor.type){
                Sensor.TYPE_ACCELEROMETER -> {
                    Log.d("ACELEROMETRO X",event.values[0].toString())
                    Log.d("ACELEROMETRO Y",event.values[1].toString())
                    Log.d("ACELEROMETRO Z",event.values[2].toString())
                }
                Sensor.TYPE_MAGNETIC_FIELD -> {

                    Log.d("MAGNETICO X",event.values[0].toString())
                    Log.d("MAGNETICO Y",event.values[1].toString())
                    Log.d("MAGNETICO Z",event.values[2].toString())
                }
            }
        }
        /*if (gravedad != null && geomagnetico != null) {
            val R = FloatArray(9)   // Matriz de rotación
            val I = FloatArray(9)   // Matriz de la inclinación
            val orientation = FloatArray(3) // Orientación

            // Calculamos la matriz de rotación
            val success = SensorManager.getRotationMatrix(R, I, gravedad, geomagnetico)

            if (success) {
                // Calculamos la orientación
                SensorManager.getOrientation(R, orientation)

                // El azimut es el ángulo de orientación del dispositivo respecto al norte
                val azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()  // Azimut

                // Ajustar el azimut a un rango de 0-360 grados (0 es el norte)
                val north = (azimuth + 360) % 360

                // Imprimimos el valor del azimut (norte)
                brujula.rotation = north -45
                Log.d("BRUJULA", "Norte: $north grados")
            }
        }*/
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

}