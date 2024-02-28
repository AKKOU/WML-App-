package com.example.wmlapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.wmlapp.databinding.FragmentLoginBinding

class Login : Fragment() {

    lateinit var bind:FragmentLoginBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bind = FragmentLoginBinding.inflate(inflater,container,false)
        bind.button8.setOnClickListener {
            login(0)
        }
        bind.imageView6.setOnClickListener {
            login(1)
        }
        bind.imageView7.setOnClickListener {
            login(1)
        }
        return bind.root
    }
    fun login(type:Int){
        if(type==0){
            if(bind.account.text.isNullOrBlank())
                bind.account.setError("內容為空")
            else if(bind.password.text.isNullOrBlank())
                bind.password.setError("內容為空")
            else{
                (activity as MainActivity).change()
                activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.fragmentContainerView3,TicketPurchase())?.commit()
            }
        }
        else{
            (activity as MainActivity).change()
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.fragmentContainerView3,TicketPurchase())?.commit()
        }
    }

}