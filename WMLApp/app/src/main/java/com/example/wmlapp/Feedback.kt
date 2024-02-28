package com.example.wmlapp

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.wmlapp.databinding.FragmentFeedbackBinding
import org.json.JSONObject
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.time.Duration
import java.time.ZonedDateTime

class Feedback : Fragment() {

    lateinit var bind:FragmentFeedbackBinding
    lateinit var RequestToken:String
    lateinit var EarliestApplyTime:String
    lateinit var ResultCode:String
    lateinit var ResultMessage:String
    lateinit var runnable:Runnable
    lateinit var responsecodes:String
    val handler = Handler(Looper.getMainLooper())
    var ratingstar:Float = 0.0F
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        EarliestApplyTime = ZonedDateTime.now().toString()
        APIGet()
        bind = FragmentFeedbackBinding.inflate(inflater,container,false)
        bind.ratingBar.setOnRatingBarChangeListener { ratingBar, rating, b ->
            if(rating<1.0f){
                bind.ratingBar.rating = 1.0f
            }
            ratingstar = rating
        }

        // 建立一個 Runnable 來在一分鐘後顯示按鈕
        runnable = Runnable {
            bind.button9.isEnabled = true
        }

        lockdown()

        // 從 SharedPreferences 中讀取開始時間
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return bind.root

        bind.button9.setOnClickListener {
            APIPost()
        }

        return bind.root
    }

    fun lockdown(){

        // 從 SharedPreferences 中讀取開始時間
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        val startTime = sharedPref.getLong("startTime", 0)

        // 計算剩餘時間
        val elapsedTime = System.currentTimeMillis() - startTime
        val remainingTime = 60000 - elapsedTime

        val timer = object :CountDownTimer(remainingTime,1000){

            override fun onTick(p0: Long) {
                bind.button9.isEnabled = false
                bind.button9.text = "剩餘秒數:${p0/1000}"
            }

            override fun onFinish() {
                bind.button9.isEnabled = true
                bind.button9.text = "提交資料 "
            }
        }

        // 如果剩餘時間大於 0，則隱藏並禁用按鈕，並在剩餘時間後顯示並啟用按鈕
        if (remainingTime > 0) {
            timer.start()
            handler.postDelayed(runnable, remainingTime)
        } else {
            bind.button9.isEnabled = true
        }

    }

    fun APIGet(){
        activity?.runOnUiThread {
            Thread{
                try {
                    val url = URL("http://10.0.2.2:5000/api/UserVoice/GetRequestToken")
                    val connection = url.openConnection()
                    connection.getInputStream().bufferedReader().use { reader->
                        val result = reader.readText()
                        activity?.runOnUiThread {
                            val jsonObject = JSONObject(result)
                            RequestToken = jsonObject.getString("RequestToken")
                            ResultCode = jsonObject.getString("ResultCode")
                            EarliestApplyTime = jsonObject.getString("EarliestApplyTime")
                            ResultMessage = jsonObject.getString("ResultMessage")

                        }
                    }
                }
                catch (e:Exception){
                    activity?.runOnUiThread {
                        Toast.makeText(requireContext(),e.toString(),Toast.LENGTH_LONG).show()
                    }
                }
            }.start()
        }
    }

    fun APIPost(){
        val stars = ratingstar
        val EarlyTime = ZonedDateTime.parse(EarliestApplyTime)
        val feedbacktext = bind.inputtext1.text
        val timenow = ZonedDateTime.now()
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return

        activity?.runOnUiThread {
            Thread{
                if (feedbacktext.isNullOrBlank()) {
                    bind.inputtext1.setError("內容為空")
                } else if (timenow.isBefore(EarlyTime)) {
                    val waitsec = Duration.between(timenow, EarlyTime)
                    activity?.runOnUiThread {
                        Toast.makeText(
                            requireContext(),
                            "請稍後${waitsec.seconds}秒再發送!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    try {
                        val url = URL("http://10.0.2.2:5000/api/UserVoice/ApplyUserVoice")
                        val connection = url.openConnection() as HttpURLConnection
                        connection.requestMethod = "POST"
                        connection.setRequestProperty("Content-Type", "application/json;utf-8")
                        connection.setRequestProperty("Accept", "application/json")
                        connection.doOutput = true

                        val jsonObject = JSONObject()
                        jsonObject.put("RequestToken", RequestToken)
                        jsonObject.put("Content", feedbacktext)
                        jsonObject.put("Score", stars)

                        val postdata = jsonObject.toString()
                        OutputStreamWriter(connection.outputStream, "UTF-8").use { writer ->
                            writer.write(postdata)
                            writer.flush()
                            writer.close()
                        }
                        val responsecode = connection.responseMessage
                        if (responsecode == "OK") {
                            activity?.runOnUiThread {
                                Toast.makeText(
                                    requireContext(),
                                    "感謝你的反饋!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                activity?.supportFragmentManager?.beginTransaction()
                                    ?.replace(R.id.fragmentContainerView3, MainPage())?.commit()
                                responsecodes = "OK"
                                // 保存當前時間到 SharedPreferences
                                with(sharedPref.edit()) {
                                    putLong("startTime", System.currentTimeMillis())
                                    apply()
                                }
                                // 在一分鐘後執行 Runnable
                                lockdown()
                                handler.postDelayed(runnable, 60000)
                            }
                        } else {
                            activity?.runOnUiThread {
                                Toast.makeText(
                                    requireContext(),
                                    "發生錯誤，請稍後在試",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } catch (e: Exception) {
                        activity?.runOnUiThread {
                            Toast.makeText(requireContext(), e.toString(), Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
            }.start()
        }
    }

}