package com.app.currency.ui.historical

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.currency.R
import com.app.currency.databinding.FragmentHistoricalBinding
import com.app.currency.network.helper.Resource
import com.app.currency.ui.historical.adapter.CurrencyHistoryAdapter
import com.app.currency.utils.withColor
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class HistoricalFragment : Fragment() {

    private var _binding: FragmentHistoricalBinding? = null
    private val binding get() = _binding!!
    private val historyViewModel: HistoricalViewModel by viewModels()

    lateinit var historyAdapter : CurrencyHistoryAdapter
    lateinit var  historyLayoutManager : LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHistoricalBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpList()


        arguments?.let {
            val args = HistoricalFragmentArgs.fromBundle(it)
            historyViewModel.getCurrencyHistory(args.from, args.to, args.amount.toDouble())
            observeData()
        }
    }


    private fun observeData(){
        historyViewModel.data.observe(this, androidx.lifecycle.Observer {
            if(it.status == Resource.Status.SUCCESS){
                it.data?.let { it1 -> historyAdapter.addItems(it1) }
                binding.progressBar.visibility = View.GONE
            }
            else if(it.status == Resource.Status.LOADING){
                binding.progressBar.visibility = View.VISIBLE
            }else if(it.status== Resource.Status.ERROR){

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
        })
    }


    private fun setUpList(){
        historyAdapter = CurrencyHistoryAdapter(mutableListOf())
        historyLayoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        binding.historyRV.apply {
            adapter = historyAdapter
            layoutManager = historyLayoutManager
        }
    }
}