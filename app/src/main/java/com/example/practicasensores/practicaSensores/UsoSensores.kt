package com.example.practicasensores.practicaSensores

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Vibrator
import android.os.VibrationEffect
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

//funcion que cambia el color te pantalla y el texto en funcion de la proximidad
@Composable
fun PracticaSensorDeProximidad(modifier: Modifier =Modifier) {
    //le pasamos el contexto de la aplicacion para que poder cargar el sensor correctamente
    val contexto = LocalContext.current
    val valor = remember { mutableStateOf<Float?>(null) }
    val controlador = contexto.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val sensor_proximidad = controlador.getDefaultSensor(Sensor.TYPE_PROXIMITY)
    //el listener tiene que ser una variable que se inicialice tarde ya que su valor esta dentro de la corrutina
     lateinit var listener: SensorEventListener
    val centimetros = remember { mutableStateOf<Float?>(0.0f) }
     //corutina para estas "escuchando" el estado del sensor t_do el rato (kotlin no  deja escribir to do junto , por que?)
    LaunchedEffect(Unit) {
        if (sensor_proximidad != null) {
             listener = object : SensorEventListener {
                //para activar la vibracion hay que añador esta anotacion y <uses-permission android:name="android.permission.VIBRATE"/> al manifest
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onSensorChanged(event: SensorEvent?) { // funcion heredada que ejecutas las acciones al cambiar lo que recibe el sensor
                    event?.values?.firstOrNull()?.let {
                        valor.value = it
                        if (it < sensor_proximidad.maximumRange) {
                            Vibracion_Sensor(contexto)
                        }
                    }
                }
                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int)
                {

                    Log.i("a ver",accuracy.toString())
                }


            }
            //pasando el ultimo argumento en high hace que tengamos mayor en el dato que recibimos
            controlador.registerListener(listener, sensor_proximidad, SensorManager.SENSOR_STATUS_ACCURACY_HIGH)
        }
    }
    //variable que usa animateColorAsState para darle una leve animacion al cambio de color
    val backgroundColor by animateColorAsState(
        targetValue = if (valor.value != null && valor.value!! < 5f) Color.Red else Color.Green
    )
    Column(modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center) {
        Text(
            text = "Proximity: ${valor.value} cm", //recordar que de esta forma  se pueden imprimir variables en textos composables
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))
        //
        Text(
            text = if (valor.value != null && valor.value!! < 5f)
                "Too Close!" else "Safe Distance",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun Vibracion_Sensor(context: Context) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (vibrator.hasVibrator()) {
        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun GreetingPreview3() {
    PracticaSensorDeProximidad(Modifier)
}