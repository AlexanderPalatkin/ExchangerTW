package com.example.exchangertw.presentation.exchanger

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.exchangertw.R
import com.example.exchangertw.databinding.FragmentExchangerBinding
import com.example.exchangertw.domain.model.Currency
import com.example.exchangertw.presentation.exchanger.adapter.CurrencyPagerAdapter
import com.example.exchangertw.presentation.exchanger.state.ExchangerState
import com.example.exchangertw.presentation.exchanger.view_model.ExchangerViewModel
import com.example.exchangertw.utils.StringResourceProvider
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ExchangerFragment : Fragment() {

    private var _binding: FragmentExchangerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ExchangerViewModel by viewModels()

    @Inject
    lateinit var stringResourceProvider: StringResourceProvider

    private lateinit var sellAdapter: CurrencyPagerAdapter
    private lateinit var buyAdapter: CurrencyPagerAdapter

    private var selectedSellCurrency: Currency? = null
    private var selectedBuyCurrency: Currency? = null

    private var sellTextWatcher: TextWatcher? = null
    private var buyTextWatcher: TextWatcher? = null
    private var isUpdatingText = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExchangerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapters()
        initViewModel()
        setupInitialEditTextListeners()
        setupEditTextListeners()
        initClickListeners()

    }

    private fun initClickListeners() {
        binding.btnExchange.setOnClickListener {
            exchangeCurrencies()
        }
    }

    private fun initAdapters() {
        sellAdapter =
            CurrencyPagerAdapter(stringResourceProvider, true)
        buyAdapter = CurrencyPagerAdapter(stringResourceProvider, false)

        setupViewPager(binding.viewPagerSell, sellAdapter, true)
        setupViewPager(binding.viewPagerBuy, buyAdapter, false)
    }

    private fun initViewModel() {
        viewModel.sellText.observe(viewLifecycleOwner) { text ->
            updateEditText(binding.viewPagerSell, text)
        }

        viewModel.buyText.observe(viewLifecycleOwner) { text ->
            updateEditText(binding.viewPagerBuy, text)
        }

        viewModel.state.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ExchangerState.Start -> {
                    Log.d("ExchangerFragment", "ExchangerState.Start")
                }

                is ExchangerState.Loading -> {
                    Log.d("ExchangerFragment", "ExchangerState.Loading")
                }

                is ExchangerState.Data -> {
                    sellAdapter.differ.submitList(state.data)
                    buyAdapter.differ.submitList(state.data)
                    Log.d("ExchangerFragment", "ExchangerState.Data: ${state.data}")
                }

                is ExchangerState.Error -> {
                    Log.d("ExchangerFragment", "ExchangerState.Error ${state.message}")
                    showErrorDialog(state.message)
                }
            }
        }

        viewModel.loadCurrencies()
    }

    private fun updateEditText(viewPager: ViewPager2, text: String) {
        val editText = viewPager.findViewById<EditText>(R.id.etAmountBuyOrSell)
        editText?.let {
            if (it.text.toString() != text) {
                it.setText(text)
            }
        }
    }

    private fun setupViewPager(
        viewPager: ViewPager2,
        adapter: CurrencyPagerAdapter,
        isSellPager: Boolean
    ) {
        viewPager.adapter = adapter

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            @SuppressLint("NotifyDataSetChanged")
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val selectedCurrency = adapter.differ.currentList[position]
                if (isSellPager) {
                    selectedSellCurrency = selectedCurrency
                    buyAdapter.selectedSellCurrency = selectedCurrency
                } else {
                    selectedBuyCurrency = selectedCurrency
                    sellAdapter.selectedBuyCurrency = selectedCurrency
                }

                sellAdapter.notifyDataSetChanged()
                buyAdapter.notifyDataSetChanged()

                val sellEditText =
                    binding.viewPagerSell.findViewById<EditText>(R.id.etAmountBuyOrSell)
                val buyEditText =
                    binding.viewPagerBuy.findViewById<EditText>(R.id.etAmountBuyOrSell)

                sellEditText?.setText(viewModel.sellText.value)
                buyEditText?.setText(viewModel.buyText.value)
            }
        })

        viewPager.setCurrentItem(0, false)
    }

    private fun setupEditTextListeners() {
        binding.viewPagerSell.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setupInitialEditTextListeners()
            }
        })

        binding.viewPagerBuy.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setupInitialEditTextListeners()
            }
        })
    }

    private fun setupInitialEditTextListeners() {
        val sellEditText = binding.viewPagerSell.findViewById<EditText>(R.id.etAmountBuyOrSell)
        val buyEditText = binding.viewPagerBuy.findViewById<EditText>(R.id.etAmountBuyOrSell)

        sellEditText?.removeTextChangedListener(sellTextWatcher)
        buyEditText?.removeTextChangedListener(buyTextWatcher)

        sellTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            @SuppressLint("DefaultLocale")
            override fun afterTextChanged(s: Editable?) {
                if (isUpdatingText) return
                isUpdatingText = true

                val sellAmount = s.toString().toDoubleOrNull()
                if (sellAmount == null || s.isNullOrEmpty()) {
                    sellEditText?.hint = stringResourceProvider.getDoubleZero()
                    buyEditText?.hint = stringResourceProvider.getDoubleZero()
                    buyEditText?.text?.clear()
                    viewModel.updateSellText("")
                    viewModel.updateBuyText("")
                } else {
                    viewModel.updateSellText(s.toString())
                    val buyAmount = calculateBuyAmount(sellAmount)
                    viewModel.updateBuyText(String.format("%.2f", buyAmount))
                }

                isUpdatingText = false
            }
        }

        buyTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            @SuppressLint("DefaultLocale")
            override fun afterTextChanged(s: Editable?) {
                if (isUpdatingText) return
                isUpdatingText = true

                val buyAmount = s.toString().toDoubleOrNull()
                if (buyAmount == null || s.isNullOrEmpty()) {
                    sellEditText?.hint = stringResourceProvider.getDoubleZero()
                    buyEditText?.hint = stringResourceProvider.getDoubleZero()
                    sellEditText?.text?.clear()
                    viewModel.updateSellText("")
                    viewModel.updateBuyText("")
                } else {
                    viewModel.updateBuyText(s.toString())
                    val sellAmount = calculateSellAmount(buyAmount)
                    viewModel.updateSellText(String.format("%.2f", sellAmount))
                }

                isUpdatingText = false
            }
        }

        sellEditText?.addTextChangedListener(sellTextWatcher)
        buyEditText?.addTextChangedListener(buyTextWatcher)
    }

    private fun calculateBuyAmount(sellAmount: Double): Double {
        val sellCurrency = selectedSellCurrency
        val buyCurrency = selectedBuyCurrency
        return if (sellCurrency != null && buyCurrency != null) {
            sellAmount * (buyCurrency.rate / sellCurrency.rate)
        } else {
            0.0
        }
    }

    private fun calculateSellAmount(buyAmount: Double): Double {
        val sellCurrency = selectedSellCurrency
        val buyCurrency = selectedBuyCurrency
        return if (sellCurrency != null && buyCurrency != null) {
            buyAmount * (sellCurrency.rate / buyCurrency.rate)
        } else {
            0.0
        }
    }

    private fun exchangeCurrencies() {
        val sellEditText = binding.viewPagerSell.findViewById<EditText>(R.id.etAmountBuyOrSell)
        val buyEditText = binding.viewPagerBuy.findViewById<EditText>(R.id.etAmountBuyOrSell)

        val sellAmount = sellEditText.text.toString().toDoubleOrNull() ?: 0.0
        val buyAmount = buyEditText.text.toString().toDoubleOrNull() ?: 0.0

        val sellCurrency = selectedSellCurrency
        val buyCurrency = selectedBuyCurrency

        if (sellCurrency != null && buyCurrency != null) {
            if (sellAmount > sellCurrency.amount) {
                showErrorDialog(stringResourceProvider.getHaveNoMoney())
                return
            }

            viewModel.updateCurrencyAmount(
                sellCurrency.code.name,
                buyCurrency.code.name,
                sellAmount,
                buyAmount
            )

            sellEditText.text.clear()
            buyEditText.text.clear()

            showExchangeSuccessDialog(
                sellCurrency = sellCurrency,
                buyCurrency = buyCurrency,
                soldAmount = sellAmount,
                boughtAmount = buyAmount
            )
        }
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(stringResourceProvider.getError())
            .setMessage(message)
            .setPositiveButton(stringResourceProvider.getOk()) { dialog, _ ->
                dialog.dismiss()
            }
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    private fun showExchangeSuccessDialog(
        sellCurrency: Currency,
        buyCurrency: Currency,
        soldAmount: Double,
        boughtAmount: Double
    ) {
        val message = """
        Успешный обмен!
        Продано: $soldAmount ${sellCurrency.code.name}
        Куплено: $boughtAmount ${buyCurrency.code.name}
    """.trimIndent()

        AlertDialog.Builder(requireContext())
            .setTitle(stringResourceProvider.getSuccessful())
            .setMessage(message)
            .setPositiveButton(stringResourceProvider.getOk()) { dialog, _ ->
                dialog.dismiss()
            }
            .setIcon(android.R.drawable.ic_dialog_info)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}