package com.example.crop_diease

import android.app.NotificationChannel
import android.app.NotificationManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class CropPrediction : AppCompatActivity() {

    var MY_PREFS_NAME = "MyPrefsFile"
    var address: String? = null
    var weather: kotlin.String? = null
    var final: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop_prediction)

        val edsoiltype = findViewById<EditText>(R.id.edsoiltype)
        val submit = findViewById<Button>(R.id.submit)
        val txtresult = findViewById<TextView>(R.id.txtresult)


        val sh = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE)
        address = sh.getString("address", "default")
        Toast.makeText(applicationContext, address, Toast.LENGTH_LONG).show()

        val sh1 = getSharedPreferences("MySharedPref", MODE_PRIVATE)
        weather = sh1.getString("weather", "default")
        Toast.makeText(this, weather.toString(), Toast.LENGTH_SHORT).show()


        submit.setOnClickListener()
        {

            val address = address.toString()
            val weather = weather.toString()
            val soiltype = edsoiltype.text.toString()

            final = address + weather + edsoiltype

            val soilcrops = HashMap<String, String>()

            soilcrops["Blacksoil"] = "Soyabean,Castor,jowar"
            soilcrops["Sandysoil"] = "Carrots, \n Tomatoes,\n Corn"
            soilcrops["Yellowsoil"] = "Potato, \n Oilseeds,\n Pulses"
            soilcrops["Lateritesoil"] = "Tea, \n Coffee,\n Rubber,\n Coconut"


//            Toast.makeText(applicationContext,soilcrops[soiltype],Toast.LENGTH_LONG).show()
            txtresult.text = soilcrops[soiltype].toString()

        }

//

    }
}