package com.example.exchangertw.presentation.exchanger.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.exchangertw.databinding.ItemCurrencyBinding
import com.example.exchangertw.domain.model.Currency
import com.example.exchangertw.domain.model.CurrencyCode
import com.example.exchangertw.utils.StringResourceProvider
import javax.inject.Inject

class CurrencyPagerAdapter @Inject constructor(
    private val stringResourceProvider: StringResourceProvider,
    private val isSellPager: Boolean
) : RecyclerView.Adapter<CurrencyPagerAdapter.ViewHolder>() {

    var selectedSellCurrency: Currency? = null
    var selectedBuyCurrency: Currency? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemCurrencyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = (differ.currentList[position])
        val selectedCurrency = if (isSellPager) selectedBuyCurrency else selectedSellCurrency
        holder.setData(item, selectedCurrency)
    }

    override fun getItemCount(): Int = differ.currentList.size

    inner class ViewHolder(private val binding: ItemCurrencyBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setData(item: Currency, selectedCurrency: Currency?) {
            with(binding) {
                tvCurrencyCode.text = item.code.name
                tvCurrencyAmount.text = item.amount.toString()

                if (isSellPager) {
                    tvCardInfo.text = stringResourceProvider.getSell()
                } else {
                    tvCardInfo.text = stringResourceProvider.getBuy()
                }

                if (selectedCurrency != null) {
                    val exchangeRate = item.calculateRate(selectedCurrency.rate)
                    tvCurrentExchangeRate.text = stringResourceProvider.getOne()
                    tvCurrentExchangeRateIcon.text = when (item.code) {
                        CurrencyCode.AUD -> stringResourceProvider.getDollarSign()
                        CurrencyCode.GBP -> stringResourceProvider.getPoundSign()
                        CurrencyCode.USD -> stringResourceProvider.getDollarSign()
                        CurrencyCode.EUR -> stringResourceProvider.getEuroSign()
                    }
                    tvAnotherCurrency.text = "%.2f".format(exchangeRate)
                    tvAnotherCurrencyIcon.text = when (selectedCurrency.code) {
                        CurrencyCode.AUD -> stringResourceProvider.getDollarSign()
                        CurrencyCode.GBP -> stringResourceProvider.getPoundSign()
                        CurrencyCode.USD -> stringResourceProvider.getDollarSign()
                        CurrencyCode.EUR -> stringResourceProvider.getEuroSign()
                    }

                } else {
                    tvCurrentExchangeRate.text = ""
                    tvCurrentExchangeRateIcon.text = ""
                    tvAnotherCurrency.text = ""
                }

            }
        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<Currency>() {
        override fun areItemsTheSame(
            oldItem: Currency,
            newItem: Currency
        ): Boolean {
            return oldItem.code == newItem.code
        }

        override fun areContentsTheSame(
            oldItem: Currency,
            newItem: Currency
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback).apply { submitList(emptyList()) }
}