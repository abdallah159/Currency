package com.app.currency.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.app.currency.R
import com.app.currency.databinding.FragmentHomeBinding
import com.app.currency.model.Rates
import com.app.currency.network.helper.Resource
import com.app.currency.utils.Utility
import com.app.currency.utils.withColor
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class HomeFragment : Fragment() {

    //I am using viewBinding to get the reference of the views
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    //Selected country string, default is Afghanistan, since its the first country listed in the spinner
    private var selectedItem1: String? = "AFN"
    private var selectedItem2: String? = "AFN"

    private var selectedEditText = 1


    private val firstCurrencyTextChangeListener = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            val numberToConvert = s.toString()

            if (numberToConvert.isEmpty() || numberToConvert == "0") {
                Snackbar.make(
                    binding.mainLayout,
                    "Input a value in text field, result will be shown in the second text field",
                    Snackbar.LENGTH_LONG
                )
                    .withColor(ContextCompat.getColor(requireContext(), R.color.dark_red))
                    .setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    .show()
            }

            //check if internet is available
            else if (!Utility.isNetworkAvailable(requireContext())) {
                Snackbar.make(
                    binding.mainLayout,
                    "You are not connected to the internet",
                    Snackbar.LENGTH_LONG
                )
                    .withColor(ContextCompat.getColor(requireContext(), R.color.dark_red))
                    .setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    .show()
            }

            //carry on and convert the value
            else {
                selectedEditText = 1
                doConversion()
            }
        }

    }

    private val secondCurrencyTextChangeListener = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            val numberToConvert = s.toString()

            if (numberToConvert.isEmpty() || numberToConvert == "0") {
                Snackbar.make(
                    binding.mainLayout,
                    "Input a value in text field, result will be shown in the second text field",
                    Snackbar.LENGTH_LONG
                )
                    .withColor(ContextCompat.getColor(requireContext(), R.color.dark_red))
                    .setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    .show()
            }

            //check if internet is available
            else if (!Utility.isNetworkAvailable(requireContext())) {
                Snackbar.make(
                    binding.mainLayout,
                    "You are not connected to the internet",
                    Snackbar.LENGTH_LONG
                )
                    .withColor(ContextCompat.getColor(requireContext(), R.color.dark_red))
                    .setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    .show()
            }

            //carry on and convert the value
            else {
                selectedEditText = 2
                doConversion()
            }
        }

    }

    //ViewModel
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Make status bar transparent
        Utility.makeStatusBarTransparent(requireActivity())

        //Initialize both Spinner
        initSpinner()

        //Listen to click events
        setUpClickListener()

        doConversion()
    }


    /**
     * This method does everything required for handling spinner (Dropdown list) - showing list of countries, handling click events on items selected.*
     */

    private fun initSpinner() {

        //get first spinner country reference in view
        val spinner1 = binding.spnFirstCountry

        //set items in the spinner i.e a list of all countries
        spinner1.setItems(getAllCountries())

        //hide key board when spinner shows (For some weird reasons, this isn't so effective as I am using a custom Material Spinner)
        spinner1.setOnClickListener {
            Utility.hideKeyboard(requireActivity())
        }

        //Handle selected item, by getting the item and storing the value in a  variable - selectedItem1
        spinner1.setOnItemSelectedListener { view, position, id, item ->
            //Set the currency code for each country as hint
            val countryCode = getCountryCode(item.toString())
            val currencySymbol = getSymbol(countryCode)
            selectedItem1 = currencySymbol
            selectedEditText = 1
            doConversion()
        }


        //get second spinner country reference in view
        val spinner2 = binding.spnSecondCountry

        //hide key board when spinner shows
        spinner1.setOnClickListener {
            Utility.hideKeyboard(requireActivity())
        }

        //set items on second spinner i.e - a list of all countries
        spinner2.setItems(getAllCountries())


        //Handle selected item, by getting the item and storing the value in a  variable - selectedItem2,
        spinner2.setOnItemSelectedListener { view, position, id, item ->
            //Set the currency code for each country as hint
            val countryCode = getCountryCode(item.toString())
            val currencySymbol = getSymbol(countryCode)
            selectedItem2 = currencySymbol
            selectedEditText = 2
            doConversion()
        }

    }


    /**
     * A method for getting a country's currency symbol from the country's code
     * e.g USA - USD
     */

    private fun getSymbol(countryCode: String?): String? {
        val availableLocales = Locale.getAvailableLocales()
        for (i in availableLocales.indices) {
            if (availableLocales[i].country == countryCode
            ) return Currency.getInstance(availableLocales[i]).currencyCode
        }
        return ""
    }


    /**
     * A method for getting a country's code from the country name
     * e.g Nigeria - NG
     */

    private fun getCountryCode(countryName: String) =
        Locale.getISOCountries().find { Locale("", it).displayCountry == countryName }


    /**
     * A method for getting all countries in the world - about 256 or so
     */

    private fun getAllCountries(): ArrayList<String> {

        val locales = Locale.getAvailableLocales()
        val countries = ArrayList<String>()
        for (locale in locales) {
            val country = locale.displayCountry
            if (country.trim { it <= ' ' }.isNotEmpty() && !countries.contains(country)) {
                countries.add(country)
            }
        }
        countries.sort()

        return countries
    }

    /**
     * A method for handling click events in the UI
     */

    private fun setUpClickListener() {

        //change button clicked
        binding.changeIV.setOnClickListener {
            // swap currency symbols
            var swapItem = selectedItem1
            selectedItem1 = selectedItem2
            selectedItem2 = swapItem

            // swap spinner country name
            val swapDisplayCountry = binding.spnFirstCountry.text
            binding.spnFirstCountry.text = binding.spnSecondCountry.text
            binding.spnSecondCountry.text = swapDisplayCountry
            if (binding.etFirstCurrency.text.isNullOrEmpty())
                return@setOnClickListener

            selectedEditText = 1
            setTextSilent(
                binding.etFirstCurrency,
                binding.etSecondCurrency.text.toString(),
                firstCurrencyTextChangeListener
            )
            doConversion()
        }

        binding.etFirstCurrency.addTextChangedListener(firstCurrencyTextChangeListener)
        binding.etSecondCurrency.addTextChangedListener(secondCurrencyTextChangeListener)

        binding.detailsBTN.setOnClickListener {
            if(selectedEditText==1){
                if(!binding.etFirstCurrency.text.isNullOrEmpty()) {
                    val direction = HomeFragmentDirections.actionHomeFragmentToHistoricalFragment(
                        selectedItem1 ?: "",
                        selectedItem2 ?: "",
                        binding.etFirstCurrency.text.toString()
                    )
                    findNavController().navigate(direction)
                }
            }else{
                if(!binding.etSecondCurrency.text.isNullOrEmpty()) {
                    val direction = HomeFragmentDirections.actionHomeFragmentToHistoricalFragment(
                        selectedItem1 ?: "",
                        selectedItem2 ?: "",
                        binding.etSecondCurrency.text.toString()
                    )
                    findNavController().navigate(direction)
                }
            }
        }

    }

    /**
     * A method that does the conversion by communicating with the API - fixer.io based on the data inputed
     * Uses viewModel and flows
     */

    private fun doConversion() {

        //hide keyboard
        Utility.hideKeyboard(requireActivity())

        //make progress bar visible
        binding.progressBar.visibility = View.VISIBLE

        //make button invisible

        //Get the data inputed
        val from = selectedItem1.toString()
        val to = selectedItem2.toString()
        val amount =
            if (selectedEditText == 1) {
                binding.etFirstCurrency.text.toString().toDouble()
            } else {
                binding.etSecondCurrency.text.toString().toDouble()
            }

        //do the conversion
        homeViewModel.getConvertedData(from, to, amount)

        //observe for changes in UI
        observeUi()

    }

    /**
     * Using coroutines flow, changes are observed and responses gotten from the API
     *
     */

    @SuppressLint("SetTextI18n")
    private fun observeUi() {


        homeViewModel.data.observe(this, androidx.lifecycle.Observer { result ->

            when (result.status) {
                Resource.Status.SUCCESS -> {
                    if (result.data?.status == "success") {

                        val map: Map<String, Rates>

                        map = result.data.rates

                        map.keys.forEach {

                            val rateForAmount = map[it]?.rate_for_amount

                            homeViewModel.convertedRate.value = rateForAmount

                            //set the value in the second edit text field
                            if (selectedEditText == 1) {
                                setTextSilent(
                                    binding.etSecondCurrency,
                                    homeViewModel.convertedRate.value.toString(),
                                    secondCurrencyTextChangeListener
                                )
                            } else {
                                setTextSilent(
                                    binding.etFirstCurrency,
                                    homeViewModel.convertedRate.value.toString(),
                                    firstCurrencyTextChangeListener
                                )
                            }
                        }

                        //stop progress bar
                        binding.progressBar.visibility = View.GONE
                    } else if (result.data?.status == "fail") {
                        val layout = binding.mainLayout
                        Snackbar.make(
                            layout,
                            "Ooops! something went wrong, Try again",
                            Snackbar.LENGTH_LONG
                        )
                            .withColor(ContextCompat.getColor(requireContext(), R.color.dark_red))
                            .setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                            .show()

                        //stop progress bar
                        binding.progressBar.visibility = View.GONE
                    }
                }
                Resource.Status.ERROR -> {

                    val layout = binding.mainLayout
                    Snackbar.make(
                        layout,
                        "Oopps! Something went wrong, Try again",
                        Snackbar.LENGTH_LONG
                    )
                        .withColor(ContextCompat.getColor(requireContext(), R.color.dark_red))
                        .setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                        .show()
                    //stop progress bar
                    binding.progressBar.visibility = View.GONE
                }

                Resource.Status.LOADING -> {
                    //stop progress bar
                    binding.progressBar.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun setTextSilent(editText: EditText, text: String, watcher: TextWatcher) {
        editText.removeTextChangedListener(watcher)
        editText.setText(text)
        editText.addTextChangedListener(watcher)
    }
}