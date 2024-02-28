package com.example.wmlapp

import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.FragmentContainerView
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Timer
import java.util.TimerTask

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val datetext = findViewById<TextView>(R.id.textView2)
        val onlinetext = findViewById<TextView>(R.id.textView5)
        val formattt = DateTimeFormatter.ofPattern("yyyy-MM-dd EEE. HH:mm")
        Timer().schedule(object :TimerTask(){
            override fun run() {
                runOnUiThread{
                    datetext.text = LocalDateTime.now().format(formattt).toString()
                    if(isOnline(this@MainActivity)){
                        onlinetext.text = "Online"
                        onlinetext.setTextColor(Color.GREEN)
                    }
                    else{
                        onlinetext.text = "Offline"
                        onlinetext.setTextColor(Color.RED)
                    }
                }
            }
        },0,1000)

    }

    override fun onBackPressed() {
        val item = supportFragmentManager.findFragmentById(R.id.fragmentContainerView3)

        when(item){
            is Collectible -> supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView3,MainPage()).commit()
            is guilding -> supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView3,MainPage()).commit()
            is TicketPurchase -> supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView3,MainPage()).commit()
            is TicketInfo -> supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView3,TicketPurchase()).commit()
            is Login -> supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView3,MainPage()).commit()
            is Feedback -> supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView3,MainPage()).commit()
            else -> finish()
        }
    }

    fun check():String{
        val text = findViewById<TextView>(R.id.textView3)

        return text.text.toString()
    }
    fun change(){
        val text = findViewById<TextView>(R.id.textView3)
        text.setText("目前身分:會員")
    }

}

fun isOnline(context:Context):Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
        val network = connectivityManager.activeNetwork ?: return false
        val activenetwork = connectivityManager.getNetworkCapabilities(network)?:return false
        return when{
            activenetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)->true
            activenetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)->true
            activenetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)->true
            else -> false
        }
    }
    else{
        val networkinfo = connectivityManager.activeNetworkInfo?:return false
        return networkinfo.isConnected
    }
}
