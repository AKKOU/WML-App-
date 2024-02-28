package com.example.wmlapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.wmlapp.databinding.FragmentMainPageBinding


class MainPage : Fragment() {

    lateinit var bind : FragmentMainPageBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        bind = FragmentMainPageBinding.inflate(inflater,container,false)
        bind.button.setOnClickListener{
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.fragmentContainerView3,Collectible())?.commit()
        }
        bind.button3.setOnClickListener{
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.fragmentContainerView3,guilding())?.commit()
        }
        bind.button2.setOnClickListener{
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.fragmentContainerView3,TicketPurchase())?.commit()
        }
        bind.button4.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.fragmentContainerView3,Feedback())?.commit()
        }

        return bind.root
    }

}