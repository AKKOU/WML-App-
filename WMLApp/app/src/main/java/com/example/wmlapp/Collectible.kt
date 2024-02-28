package com.example.wmlapp

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ClipData
import android.content.ClipData.Item
import android.content.Context
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.webkit.WebView
import android.widget.GridView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.PopupWindow
import android.widget.RemoteViews
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.NotificationCompat.Action
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.ResourcesCompat.ThemeCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.children
import androidx.core.view.size
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutParams
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.wmlapp.databinding.FragmentCollectibleBinding
import com.google.android.flexbox.FlexboxLayout
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import kotlin.math.min

class Collectible : Fragment() {

    lateinit var bind :FragmentCollectibleBinding
    lateinit var mydata:JSONArray
    var showdata:Int=0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bind = FragmentCollectibleBinding.inflate(inflater,container,false)

        // Load the JSON Array
        val jsonstring = loadjsonfromasset()
        mydata = JSONArray(jsonstring)
        showdata = mydata.length()
        //viewpager2
        bind.searchButton.setOnClickListener {
            val search = bind.searchtext.text.toString()

            if(search.isNullOrBlank()){
                mydata = JSONArray(jsonstring)
            }
            else{
                mydata = filteritem(JSONArray(jsonstring),search)
            }
            showdata = mydata.length()
            bind.collecpager.adapter = MyAdapter(mydata)

        }
        bind.collecpager.orientation = ViewPager2.ORIENTATION_VERTICAL
        bind.collecpager.adapter = MyAdapter(mydata)

        return bind.root
    }

    private fun filteritem(jsonArray: JSONArray,search:String):JSONArray{
        var filteredarray = JSONArray()
        for(i in 0 until jsonArray.length()){
            val item = jsonArray.getJSONObject(i)
            if(item.has("Label") && item.getString("Label").contains(search,ignoreCase = true)){
                filteredarray.put(item)
            }
        }
        return filteredarray
    }


    private fun  loadjsonfromasset():String?{
        return try {
            val inputStream = context?.assets?.open("collectible.json")
            val size = inputStream?.available()
            val buffer = ByteArray(size?:0)
            inputStream?.read(buffer)
            inputStream?.close()
            String(buffer,Charsets.UTF_8)
        }
        catch (ex:IOException){
            ex.printStackTrace()
            return null
        }
    }

    inner class MyAdapter(private val data:JSONArray):RecyclerView.Adapter<MyViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.collective,parent,false)
            return MyViewHolder(view)
        }

        override fun getItemCount(): Int {
            return (showdata+5)/6
        }

        @SuppressLint("ClickableViewAccessibility")
        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            for(i in 0 until 6){
                val index = position*6+i
                if (index<data.length()){
                    val item = data.getJSONObject(index)
                    val urlstring = JSONArray(item.getString("Images"))
                    val image = urlstring.getJSONObject(0)

                    holder.labels[i].text = item.getString("Label")
                    holder.museums[i].text = item.getString("Museum")
                    holder.webViews[i].loadUrl(image.getString("thumbnail"))
                    holder.webViews[i].settings.loadWithOverviewMode = true
                    holder.webViews[i].settings.useWideViewPort = true

                    holder.popupMenus[i] = PopupMenu(holder.bts[i].context,holder.bts[i])
                    holder.popupMenus[i].inflate(R.menu.menu1)
                    holder.popupMenus[i].gravity = Gravity.RIGHT
                    holder.popupMenus[i].menu.findItem(R.id.menuitem1)?.setTitle("WikiCode:${item.getString("WikiCode")}")
                    holder.popupMenus[i].menu.findItem(R.id.menuitem2)?.setTitle("Nature:${item.getString("Nature")}")
                    holder.popupMenus[i].menu.findItem(R.id.menuitem3)?.setTitle("RegisterNumber:${item.getString("RegisterNumber")}")

                    holder.dialog[i].setContentView(R.layout.imageshow)
                    holder.dialog[i].findViewById<WebView>(R.id.webView8).loadUrl(image.getString("thumbnail"))
                    holder.dialog[i].findViewById<WebView>(R.id.webView8).settings.useWideViewPort = true
                    holder.dialog[i].findViewById<WebView>(R.id.webView8).settings.loadWithOverviewMode = true
                    holder.dialog[i].findViewById<TextView>(R.id.copyrighttext).text = image.getString("copyright")
                    holder.dialog[i].findViewById<TextView>(R.id.pagenumber).text = "1|${urlstring.length()}"
                    holder.dialog[i].findViewById<ImageButton>(R.id.imageButton).setOnClickListener{
                        holder.dialog[i].dismiss()
                    }
                    val flexbox = holder.dialog[i].findViewById<FlexboxLayout>(R.id.flexbox)
                    for(j in 0 until urlstring.length()){
                        val imagelist = urlstring.getJSONObject(j)
                        val webView = WebView(requireContext())
                        webView.settings.useWideViewPort = true
                        webView.settings.loadWithOverviewMode = true

                        val layoutParams = FlexboxLayout.LayoutParams(
                            FlexboxLayout.LayoutParams.WRAP_CONTENT,
                            FlexboxLayout.LayoutParams.WRAP_CONTENT
                        )
                        layoutParams.flexBasisPercent = 0.33f
                        webView.layoutParams = layoutParams

                        webView.loadUrl(imagelist.getString("thumbnail"))
                        flexbox.addView(webView)
                        webView.setOnTouchListener{v,event->
                            if(event.action == MotionEvent.ACTION_DOWN){
                                holder.dialog[i].findViewById<WebView>(R.id.webView8).loadUrl(imagelist.getString("url"))
                                holder.dialog[i].findViewById<TextView>(R.id.pagenumber).text = "${j+1}|${urlstring.length()}"
                            }
                            false
                        }
                    }

                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val webViewIds = listOf(R.id.webView2, R.id.webView3, R.id.webView4, R.id.webView5, R.id.webView6, R.id.webView7)
        val webViews = webViewIds.map { id -> itemView.findViewById<WebView>(id) }

        val labelIds = listOf(R.id.textView23, R.id.textView27, R.id.textView31, R.id.textView34, R.id.textView35, R.id.textView40)
        val labels = labelIds.map { id -> itemView.findViewById<TextView>(id) }

        val museumIds = listOf(R.id.textView24, R.id.textView28, R.id.textView32, R.id.textView33, R.id.textView36, R.id.textView39)
        val museums = museumIds.map { id -> itemView.findViewById<TextView>(id) }

        val btIds = listOf(R.id.imageView11, R.id.imageView8, R.id.imageView9, R.id.imageView12, R.id.imageView13, R.id.imageView15)
        val bts = btIds.map { id -> itemView.findViewById<ImageView>(id) }

        val popupMenus = MutableList(6) { PopupMenu(itemView.context, itemView) }

        val dialog = MutableList(6){Dialog(requireContext())}

        init {
            val nums = BooleanArray(6) { false }

            for(i in 0 until 6){
                bts[i].setOnClickListener {
                        view ->
                    popupMenus[i].show()
                    if(nums[i]){
                        bts[i].setImageResource(R.drawable.baseline_expand_more_24)
                        nums[i] = !nums[i]
                    }
                    else{
                        bts[i].setImageResource(R.drawable.baseline_expand_less_24)
                        nums[i] = !nums[i]
                    }
                    popupMenus[i].setOnDismissListener {
                        if(nums[i]){
                            bts[i].setImageResource(R.drawable.baseline_expand_more_24)
                        }
                        else{
                            bts[i].setImageResource(R.drawable.baseline_expand_less_24)
                        }
                        nums[i] = !nums[i]
                    }
                }

                webViews[i].setOnTouchListener { v, event ->
                    if (event.action == MotionEvent.ACTION_DOWN) {
                        // 在這裡處理點擊事件
                        dialog[i].show()
                    }
                    false
                }


            }

        }
    }
}