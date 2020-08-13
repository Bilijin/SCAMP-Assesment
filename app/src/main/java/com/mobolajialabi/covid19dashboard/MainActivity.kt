package com.mobolajialabi.covid19dashboard

import android.os.Bundle
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL
import java.text.NumberFormat
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    private val data = listOf("Active Cases","Recoveries","Deaths")

    private lateinit var colors: List<Int>
    private lateinit var pieChart: PieChart
    private lateinit var recyclerViewAdapter: MyRecyclerViewAdapter
    private lateinit var recyclerView :RecyclerView
    private lateinit var searchView : SearchView

    private  var countries: ArrayList<CountryItem> = ArrayList()
    private lateinit var globalJson : JSONObject
    private lateinit var jsonCountry : JSONObject

    private lateinit var rank : ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pieChart  = global_pie_chart
        colors  = listOf(getColor(R.color.yellow),getColor(R.color.green), getColor(R.color.red))
        recyclerView = recycler_view
        searchView = search_view
        rank = rank_image

        recyclerViewAdapter = MyRecyclerViewAdapter(countries)

        run("https://api.covid19api.com/summary")

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(queryText: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(queryText: String): Boolean {
                recyclerViewAdapter.filter.filter(queryText)
                return false
            }
        })

        rank.setOnClickListener { view ->
            val popUp = PopupMenu(view?.context, view)
            val inflater : MenuInflater = popUp.menuInflater
            inflater.inflate(R.menu.rank_menu,popUp.menu)
            popUp.show()
        }
    }

    private fun setupPieChart() {
        val pieEntries = ArrayList<PieEntry>()

        val obj = globalJson.getJSONObject("Global")

        val confirmed = obj.getInt("TotalConfirmed")
        val recovered = obj.getInt("TotalRecovered")
        val deaths = obj.getInt("TotalDeaths")
        val active = confirmed - (recovered + deaths)

        pieEntries.add(PieEntry(active.toFloat(),data[0]))
        pieEntries.add(PieEntry(recovered.toFloat(),data[1]))
        pieEntries.add(PieEntry(deaths.toFloat(),data[2]))

        val pieDataSet = PieDataSet(pieEntries, "Global Statistics")
        pieDataSet.colors = colors
        val pieData = PieData(pieDataSet)
        pieData.setValueTextColor(getColor(R.color.white))
        pieData.setValueTextSize(15F)

        val numberFormat = NumberFormat.getInstance()
        numberFormat.isGroupingUsed = true

        pieChart.holeRadius = 0F
        pieChart.setTransparentCircleAlpha(0)
        pieChart.setEntryLabelTextSize(18F)
        pieChart.description.text = numberFormat.format(confirmed) + " Confirmed Cases"
        pieChart.description.textSize = 15F
        pieChart.data = pieData
        pieChart.notifyDataSetChanged()
        pieChart.invalidate()
    }

    private fun loadCountries(){
        val jsonArray: JSONArray = jsonCountry.getJSONArray("Countries")

        var i = 0
        while (i<jsonArray.length()){
            val jsonObjectInfo = jsonArray.getJSONObject(i)
            val countryItem= CountryItem("i",0,0,0)
            countryItem.name = jsonObjectInfo.getString("Country")
            countryItem.cases = jsonObjectInfo.getInt("TotalConfirmed")
            countryItem.recoveries = jsonObjectInfo.getInt("TotalRecovered")
            countryItem.deaths = jsonObjectInfo.getInt("TotalDeaths")
            countries.add(countryItem)
            i++
        }
        recyclerView.adapter = recyclerViewAdapter
        val linearLayoutManager = LinearLayoutManager(parent,LinearLayoutManager.VERTICAL,false)
        recyclerView.layoutManager = linearLayoutManager
    }

    private fun run(url:String) {
        doAsync {
            val countryJson = URL(url).readText()

            uiThread {
                globalJson = JSONObject(countryJson)
                jsonCountry = JSONObject(countryJson)
                setupPieChart()
                loadCountries()
            }
        }
    }

    fun rank(item: MenuItem) {
        when (item.itemId) {
            R.id.rank_cases_descend -> recyclerViewAdapter.rankCases()
            R.id.rank_cases_ascend -> recyclerViewAdapter.rankCasesAscending()
            R.id.reset -> recyclerViewAdapter.reset()
        }
    }
}