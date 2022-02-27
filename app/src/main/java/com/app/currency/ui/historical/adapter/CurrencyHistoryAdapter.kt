package com.app.currency.ui.historical.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.currency.R
import com.app.currency.model.ApiResponse
import kotlinx.android.synthetic.main.item_currency_rate.view.*

class CurrencyHistoryAdapter(
    var items: MutableList<ApiResponse> = mutableListOf()
) : RecyclerView.Adapter<CurrencyHistoryAdapter.CurrencyHistoryVH>() {

    class CurrencyHistoryVH(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyHistoryVH {
        return CurrencyHistoryVH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_currency_rate, parent,false)
        )
    }

    override fun onBindViewHolder(holder: CurrencyHistoryVH, position: Int) {
        with(holder.itemView) {
            dateTV.text = "Date : ${items[position].updated_date}"
            fromTV.text = "From : ${items[position].base_currency_name}"
            toTV.text = "To : ${items[position].rates[items[position].rates.keys.first()]?.currency_name}"
            amountTV.text = "Amount : ${items[position].rates[items[position].rates.keys.first()]?.rate_for_amount}"
        }
    }

    fun addItems(fqs: MutableList<ApiResponse>) {
        this.items.clear()
        this.items.addAll(fqs)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return items.size
    }
}
