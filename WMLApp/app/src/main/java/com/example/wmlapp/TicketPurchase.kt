package com.example.wmlapp

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.wmlapp.databinding.FragmentTicketPurchaseBinding

class TicketPurchase : Fragment() {

    lateinit var bind:FragmentTicketPurchaseBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val username = (activity as MainActivity).check()
        if(username=="目前身分:訪客")
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.fragmentContainerView3,Login())?.commit()
        var num1:Int=0
        var num2:Int=0
        var num3:Int=0
        var num4:Int=0
        var total:Int=0
        bind = FragmentTicketPurchaseBinding.inflate(inflater,container,false)
        bind.button5.setOnClickListener{
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.fragmentContainerView3,TicketInfo())?.commit()
        }
        bind.radioButton.isChecked = true
        bind.picker1.maxValue = 100
        bind.picker1.minValue = 0
        bind.picker2.minValue = 0
        bind.picker2.maxValue = 100
        bind.picker3.minValue = 0
        bind.picker3.maxValue = 100
        bind.picker4.minValue = 0
        bind.picker4.maxValue = 100

        bind.picker1.setOnValueChangedListener { numberPicker, i, i2 ->
            num1 = numberPicker.value *17
            total = num1+num2+num3+num4
            bind.textView6.text = "羅浮宮門票:€${num1}"
            bind.textView10.text = "總額:€${total}"
        }
        bind.picker2.setOnValueChangedListener { numberPicker, i, i2 ->
            num2 = numberPicker.value *48
            total = num1+num2+num3+num4
            bind.textView7.text = "巴黎博物館通行證2日券:€${num2}"
            bind.textView10.text = "總額:€${total}"
        }
        bind.picker3.setOnValueChangedListener { numberPicker, i, i2 ->
            num3 = numberPicker.value *62
            total = num1+num2+num3+num4
            bind.textView8.text = "巴黎博物館通行證4日券:€${num3}"
            bind.textView10.text = "總額:€${total}"
        }
        bind.picker4.setOnValueChangedListener { numberPicker, i, i2 ->
            num4 = numberPicker.value *74
            total = num1+num2+num3+num4
            bind.textView9.text = "巴黎博物館通行證6日券:€${num4}"
            bind.textView10.text = "總額:€${total}"
        }

        bind.button7.setOnClickListener {
            if(bind.name.text.isNullOrBlank()){
                bind.name.setError("內容不可為空")
            }
            else if(bind.phone.text.isNullOrBlank()){
                bind.name.setError("內容不可為空")
            }
            else if(bind.mail.text.isNullOrBlank()){
                bind.name.setError("內容不可為空")
            }
            else if(total==0){
                Toast.makeText(requireContext(),"請選擇一樣商品下單",Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(requireContext(),"請自行截圖保存",Toast.LENGTH_LONG).show()
                if(bind.radioButton2.isChecked){
                    creditcard()
                }
                else
                    showdialog()
            }
        }
        return bind.root
    }

    fun creditcard(){
        val dialog = AlertDialog.Builder(requireContext())
        dialog.setTitle("輸入信用卡資訊")
        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.VERTICAL

        val input1 = EditText(requireContext())
        input1.hint = "信用卡卡號"
        input1.inputType = InputType.TYPE_CLASS_NUMBER
        layout.addView(input1)

        val input2 = EditText(requireContext())
        input2.hint = "到期日"
        input2.inputType = InputType.TYPE_NUMBER_FLAG_SIGNED
        layout.addView(input2)

        val input3 = EditText(requireContext())
        input3.hint = "安全碼"
        input3.inputType = InputType.TYPE_CLASS_NUMBER
        layout.addView(input3)

        dialog.setView(layout)
        dialog.setPositiveButton("確定"){
            DialogInterface,i->
            showdialog()
        }
        dialog.setNegativeButton("取消"){
            DialogInterface,i->
        }
        dialog.show()
    }

    fun showdialog(){
        val dialog = Dialog(requireContext())
        var payment:String
        dialog.setContentView(R.layout.paymentdialog)
        val text1 = dialog.findViewById<TextView>(R.id.textView11)
        val text2 = dialog.findViewById<TextView>(R.id.textView12)
        val text3 = dialog.findViewById<TextView>(R.id.textView13)
        val text4 = dialog.findViewById<TextView>(R.id.textView14)
        val text5 = dialog.findViewById<TextView>(R.id.textView19)
        val text6 = dialog.findViewById<TextView>(R.id.textView18)
        val text7 = dialog.findViewById<TextView>(R.id.textView17)
        val text8 = dialog.findViewById<TextView>(R.id.textView16)
        val text9 = dialog.findViewById<TextView>(R.id.textView20)

        if(bind.radioButton.isChecked)
            payment = "現金付款"
        else
            payment = "信用卡"

        text1.text = "姓名:${bind.name.text}"
        text2.text = "電話:${bind.phone.text}"
        text3.text = "電子郵件:${bind.mail.text}"
        text4.text = "付款方式:${payment}"
        text5.text = bind.textView6.text
        text6.text = bind.textView7.text
        text7.text = bind.textView8.text
        text8.text = bind.textView9.text
        text9.text = bind.textView10.text
        dialog.show()
    }

}