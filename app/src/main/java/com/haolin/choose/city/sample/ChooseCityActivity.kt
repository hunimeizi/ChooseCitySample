package com.haolin.choose.city.sample

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.appcompat.app.AppCompatActivity
import com.haolin.select.city.adapter.CityListAdapter
import com.haolin.select.city.adapter.ResultListAdapter
import com.haolin.select.city.db.DBManager
import com.haolin.select.city.utils.LocateState
import com.haolin.select.city.view.PinnedSectionListView
import com.haolin.select.city.view.SideLetterBar
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ChooseCityActivity : AppCompatActivity() {

    private var mListView: PinnedSectionListView? = null
    private var mResultListView: ListView? = null
    private var mLetterBar: SideLetterBar? = null
    private var searchBox: EditText? = null
    private var clearBtn: ImageView? = null
    private var emptyView: ViewGroup? = null

    private val mCityAdapter by lazy { CityListAdapter(this) }
    private val mResultAdapter by lazy { ResultListAdapter(this, null) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_city)

        mListView = findViewById(R.id.listview_all_city)
        mResultListView = findViewById(R.id.listview_search_result)
        mListView!!.adapter = mCityAdapter
        mResultListView!!.adapter = mResultAdapter
        mLetterBar = findViewById(R.id.side_letter_bar)
        val overlay: TextView = findViewById(R.id.tv_letter_overlay)
        emptyView = findViewById(R.id.empty_view)
        searchBox = findViewById(R.id.et_search)
        clearBtn = findViewById(R.id.iv_search_clear)

        mLetterBar!!.setOverlay(overlay)

        clearBtn!!.setOnClickListener {
            searchBox!!.setText("")
            clearBtn!!.visibility = View.GONE
            emptyView!!.visibility = View.GONE
            mResultListView!!.visibility = View.GONE
        }

        mCityAdapter.setOnCityClickListener(object : CityListAdapter.OnCityClickListener {
            override fun onCityClick(name: String?, cityId: String?) {
                back(name,cityId)
            }

            override fun onLocateClick() {
                mCityAdapter.updateLocateState(LocateState.LOCATING, null, null)
                //todo 前去定位 模拟下
                emptyView!!.postDelayed({
                    mCityAdapter.updateLocateState(LocateState.SUCCESS, "北京", "131")
                }, 3000)
            }
        })

        initLocalView()
        GlobalScope.launch(Dispatchers.Main) {
            val dbManager = DBManager(this@ChooseCityActivity)
            dbManager.copyDBFile()
            mCityAdapter.addListCities(dbManager.getAllCities())
            mCityAdapter.notifyDataSetChanged()
        }
        emptyView!!.postDelayed({
            mCityAdapter.updateLocateState(LocateState.SUCCESS, "北京", "131")
        }, 3000)
    }


    private fun initLocalView() {
        mLetterBar!!.setOnLetterChangedListener { letter ->
            val position = mCityAdapter.getLetterPosition(letter)
            mListView!!.setSelection(position)
        }
        searchBox!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                val keyword = s.toString()
                if (TextUtils.isEmpty(keyword)) {
                    clearBtn!!.visibility = View.GONE
                    emptyView!!.visibility = View.GONE
                    mResultListView!!.visibility = View.GONE
                } else {
                    clearBtn!!.visibility = View.VISIBLE
                    mResultListView!!.visibility = View.VISIBLE
                    GlobalScope.launch(Dispatchers.Main) {
                        val mAllCities = DBManager(this@ChooseCityActivity).searchCity(keyword)
                        if (mAllCities.isEmpty()) {
                            emptyView!!.visibility = View.VISIBLE
                            return@launch
                        }
                        emptyView!!.visibility = View.GONE
                        mResultAdapter.changeData(mAllCities)
                    }
                }
            }
        })
        mResultListView!!.onItemClickListener =
            OnItemClickListener { _: AdapterView<*>?, _: View?, position: Int, _: Long ->
                back(
                    mResultAdapter.getItem(position).name,
                    mResultAdapter.getItem(position).cityid
                )
            }
    }
    private fun back(city: String?, cityId: String?) {
        val data = Intent()
        val bundle = Bundle()
        bundle.putString("city", city)
        bundle.putString("cityId", cityId)
        data.putExtras(bundle)
        setResult(RESULT_OK, data)
        finish()
    }
}