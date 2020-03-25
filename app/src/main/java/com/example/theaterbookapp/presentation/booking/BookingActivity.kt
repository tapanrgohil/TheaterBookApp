package com.example.theaterbookapp.presentation.booking

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.theaterbookapp.R
import com.example.theaterbookapp.presentation.BaseActivity
import com.example.theaterbookapp.presentation.booking.adpter.MainAdapter
import com.example.theaterbookapp.presentation.booking.adpter.SelectedSeatAdapter
import com.example.theaterbookapp.utils.observe
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Main and only activity of application
 */
class BookingActivity : BaseActivity<BookingViewModel>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpViews()
    }

    override fun onResume() {
        super.onResume()
        getViewModel().getBookingDetails()//Api calling
    }


    //Setup adapters and layoutManagers
    private fun setUpViews() {
        rvTheater?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = MainAdapter(){ zone, seat ->
                if(getViewModel().onSeatSelect(zone,seat)){
                    getViewModel().updateList(zone,seat)
                }

            }
        }

        rvSelected?.apply {
            layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
            itemAnimator = DefaultItemAnimator()
            adapter = SelectedSeatAdapter()
        }
    }

    //Attaching livedata from viewmodel
    override fun attachLiveData() {
        observe(getViewModel().getBookingLiveData()) {
            it?.apply data@{
                setDataToAdapters(this)
            }
        }

        observe(getViewModel().getSelectedSeatLiveData()){
            it?.apply {
                (rvSelected.adapter as? SelectedSeatAdapter)?.apply adapter@ {
                    setList(this@apply.toMutableList())
                }
            }
        }
    }

    /**
     * set data to adapters
     */
    private fun setDataToAdapters(zones: List<Zone>) {
        val list = arrayListOf<MainAdapterModel>()
        zones.forEach {
            list.add(MainAdapterModel.ZoneView(it))
            list.add(MainAdapterModel.EmptyView())
        }
        (rvTheater.adapter as? MainAdapter)?.apply {
            setData(list)
        }
    }
}
