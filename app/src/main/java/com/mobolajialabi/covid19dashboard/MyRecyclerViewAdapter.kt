package com.mobolajialabi.covid19dashboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.country_item.view.*
import java.util.*
import kotlin.collections.ArrayList

class MyRecyclerViewAdapter(private val countries: ArrayList<CountryItem>) :
    RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder>(), Filterable {

    private var fullCountries = ArrayList<CountryItem>()
    private var c = ArrayList<CountryItem>()

    init {
        fullCountries = countries
        c = countries
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.country_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val countryItem: CountryItem = fullCountries[position]

        holder.cases.text = countryItem.cases.toString()
        holder.countryName.text = countryItem.name
        holder.recoveries.text = countryItem.recoveries.toString()
        holder.deaths.text = countryItem.deaths.toString()
    }

    override fun getItemCount(): Int {
        return fullCountries.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private var view: View = itemView

        val countryName: TextView = view.country
        val cases: TextView = view.recorded_cases
        val recoveries: TextView = view.recoveries
        val deaths: TextView = view.deaths

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(itemView: View) {
            val context = itemView.context
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val searchQuery = constraint.toString()
                fullCountries = if (searchQuery.isEmpty()) {
                    countries
                } else {
                    val resultList = ArrayList<CountryItem>()
                    for (country in countries) {
                        if (country.name.toLowerCase(Locale.ROOT)
                                .contains(searchQuery.toLowerCase(Locale.ROOT))
                        ) {
                            resultList.add(country)
                        }
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = fullCountries
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                fullCountries = results?.values as ArrayList<CountryItem>
                notifyDataSetChanged()
            }
        }
    }

    fun rankCases(){
        var temp: CountryItem
        for (i in 0 until fullCountries.size) {

            for (j in (i + 1) until fullCountries.size) {
                if (fullCountries[i].cases < fullCountries[j].cases) {
                    temp = fullCountries[i]
                    fullCountries[i] = fullCountries[j]
                    fullCountries[j] = temp
                }
            }
        }
        notifyDataSetChanged()
    }

    fun rankCasesAscending(){
        var temp: CountryItem
        for (i in 0 until fullCountries.size) {

            for (j in (i + 1) until fullCountries.size) {
                if (fullCountries[i].cases > fullCountries[j].cases) {
                    temp = fullCountries[i]
                    fullCountries[i] = fullCountries[j]
                    fullCountries[j] = temp
                }
            }
        }
        notifyDataSetChanged()
    }

    fun reset() {
        var temp: CountryItem
        for (i in 0 until fullCountries.size) {

            for (j in (i + 1) until fullCountries.size) {
                if (fullCountries[i].name > fullCountries[j].name) {
                    temp = fullCountries[i]
                    fullCountries[i] = fullCountries[j]
                    fullCountries[j] = temp
                }
            }
        }
        notifyDataSetChanged()
    }
}

