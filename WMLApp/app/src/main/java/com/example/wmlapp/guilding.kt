package com.example.wmlapp

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.RemoteViews
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.wmlapp.databinding.FragmentGuildingBinding

class guilding : Fragment() {

    lateinit var bind:FragmentGuildingBinding
    lateinit var viewPager:ViewPager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bind = FragmentGuildingBinding.inflate(inflater,container,false)
        val videoplayer = bind.videoView
        videoplayer.start()
        videoplayer.setVideoPath("android.resource://"+activity?.packageName+"/"+R.raw.louvreintroduction)
        videoplayer.setOnCompletionListener {
            videoplayer.start()
        }

        videoplayer.setOnClickListener{
            if(videoplayer.isPlaying)
                videoplayer.pause()
            else
                videoplayer.start()
        }

        bind.guild1.setOnClickListener {
            Showdialogwithviewpager(12)
        }
        bind.guild2.setOnClickListener {
            Showdialogwithviewpager(13)
        }
        bind.guild3.setOnClickListener {
            Showdialogwithviewpager(14)
        }
        bind.guild4.setOnClickListener {
            Showdialogwithviewpager(15)
        }
        bind.guild5.setOnClickListener {
            Showdialogwithviewpager(16)
        }
        bind.guild6.setOnClickListener {
            Showdialogwithviewpager(17)
        }
        return bind.root
    }

    private fun Showdialogwithviewpager(position: Int){
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialogshow)
        viewPager =dialog.findViewById(R.id.viewpager1)
        setupviewpager()
        viewPager.setCurrentItem(position)
        dialog.show()
    }

    private fun setupviewpager(){
        val webview1 = WebView(requireContext())
        webview1.loadUrl("file:///android_asset/exhibition1.png")
        val webview2 = WebView(requireContext())
        webview2.loadUrl("file:///android_asset/exhibition2.png")
        val webview3 = WebView(requireContext())
        webview3.loadUrl("file:///android_asset/exhibition3.png")
        val webview4 = WebView(requireContext())
        webview4.loadUrl("file:///android_asset/exhibition4.png")
        val webview5 = WebView(requireContext())
        webview5.loadUrl("file:///android_asset/exhibition5.png")
        val webview6 = WebView(requireContext())
        webview6.loadUrl("file:///android_asset/exhibition6.png")
        val views = listOf<WebView>(webview1,webview2,webview3,webview4,webview5,webview6)
        for (i in 0 until views.count()){
            views[i].settings.builtInZoomControls = true
        }
        val adapter = MyPagerAdapter(views)
        viewPager.adapter = adapter

    }

    class MyPagerAdapter(private val views: List<View>):PagerAdapter(){
        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view == `object`
        }

        override fun getCount(): Int {
            return Integer.MAX_VALUE
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val fake = position%views.size
            val view = views[fake]
            container.addView(view)
            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }
    }
}