package com.example.wmlapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.wmlapp.databinding.FragmentTicketInfoBinding

class TicketInfo : Fragment() {

    lateinit var bind:FragmentTicketInfoBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bind = FragmentTicketInfoBinding.inflate(inflater,container,false)
        bind.webView.loadUrl("file:///android_asset/ticket.html")
        bind.button6.setOnClickListener{
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.fragmentContainerView3,TicketPurchase())?.commit()
        }
        return bind.root
    }
}