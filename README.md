# ChooseCitySample - 选择城市
### 使用gradle
```js
repositories {
  google()
  mavenCentral()
}

dependencies {
  implementation 'io.github.hunimeizi:haolinSelectTheCity:1.0.0'
}
```
### 需要用到的权限-获取手机读写权限
```js
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```
### 混淆
```js
-keep class com.haolin.select.city.bean.**{*;}
```
### 使用方法
1.布局 里面的内容直接拿来使用就行
```js
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#F3F3F3"
    android:orientation="vertical">

    <include layout="@layout/view_search_city" />

    <View
        android:layout_width="match_parent"
        android:layout_height="1px"
        android:background="#cccccc" />

    <RelativeLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="@color/white">

        <com.haolin.select.city.view.PinnedSectionListView
            android:id="@+id/listview_all_city"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:cacheColorHint="@android:color/transparent"
            android:divider="@android:color/transparent"
            android:dividerHeight="0dp"
            android:listSelector="@android:color/transparent"
            android:scrollbars="none" />

        <TextView
            android:id="@+id/tv_letter_overlay"
            android:layout_width="120dp"
            android:layout_height="120dp"
            android:layout_centerInParent="true"
            android:background="@drawable/overlay_bg"
            android:gravity="center"
            android:textColor="@android:color/white"
            android:textSize="48sp"
            android:textStyle="bold"
            android:visibility="gone" />

        <com.haolin.select.city.view.SideLetterBar
            android:id="@+id/side_letter_bar"
            android:layout_width="36dp"
            android:layout_height="match_parent"
            android:layout_alignParentRight="true"
            tools:ignore="RtlHardcoded" />

        <ListView
            android:id="@+id/listview_search_result"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:background="@android:color/white"
            android:cacheColorHint="@android:color/transparent"
            android:divider="@android:color/transparent"
            android:dividerHeight="0dp"
            android:listSelector="@android:color/transparent"
            android:visibility="gone" />

        <include layout="@layout/view_no_search_result" />
    </RelativeLayout>
</LinearLayout>
```
2.声明
```js
private var mListView: PinnedSectionListView? = null
    private var mResultListView: ListView? = null
    private var mLetterBar: SideLetterBar? = null
    private var searchBox: EditText? = null
    private var clearBtn: ImageView? = null
    private var emptyView: ViewGroup? = null
    private val mCityAdapter by lazy { CityListAdapter(this) }
    private val mResultAdapter by lazy { ResultListAdapter(this, null) }

```
3.findViewById
```js
mListView = findViewById(R.id.listview_all_city)
mResultListView = findViewById(R.id.listview_search_result)
mListView!!.adapter = mCityAdapter
ResultListView!!.adapter = mResultAdapter
mLetterBar = findViewById(R.id.side_letter_bar)
val overlay: TextView = findViewById(R.id.tv_letter_overlay)
emptyView = findViewById(R.id.empty_view)
searchBox = findViewById(R.id.et_search)
clearBtn = findViewById(R.id.iv_search_clear)
```
4.设置SideLetterBar数据
```js
mLetterBar!!.setOverlay(overlay)
```
4.设置搜索框清空数据操作
```js
clearBtn!!.setOnClickListener {
            searchBox!!.setText("")
            clearBtn!!.visibility = View.GONE
            emptyView!!.visibility = View.GONE
            mResultListView!!.visibility = View.GONE
        }
```
5.设置重新定位点击事件
```js
mCityAdapter.setOnCityClickListener(object : CityListAdapter.OnCityClickListener {
            override fun onCityClick(name: String?, cityId: String?) {
                back(name,cityId) // 代表定位成功 将数据返回到上一页
            }

            override fun onLocateClick() {
                mCityAdapter.updateLocateState(LocateState.LOCATING, null, null)
                //todo 前去定位 模拟下
                emptyView!!.postDelayed({
                    mCityAdapter.updateLocateState(LocateState.SUCCESS, "北京", "131")
                }, 3000)
            }
        })
```
6.读取assets里的数据 设置数据
```js
GlobalScope.launch(Dispatchers.Main) {
            val dbManager = DBManager(this@ChooseCityActivity)
            dbManager.copyDBFile()
            mCityAdapter.addListCities(dbManager.getAllCities())
            mCityAdapter.notifyDataSetChanged()
        }
```
7.模拟定位
```js
emptyView!!.postDelayed({
            mCityAdapter.updateLocateState(LocateState.SUCCESS, "北京", "131")
        }, 3000)
```
8.联想搜索
```js
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
```
9.数据返回方法
```js
private fun back(city: String?, cityId: String?) {
        val data = Intent()
        val bundle = Bundle()
        bundle.putString("city", city)
        bundle.putString("cityId", cityId)
        data.putExtras(bundle)
        setResult(RESULT_OK, data)
        finish()
    }
```
10.回调
```js
override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 103 && resultCode == RESULT_OK && data != null) {
            val bundle = data.extras ?: return
            tvChooseCity?.text = bundle.getString("city")
        }
    }
```
#### 内嵌上传 Maven Central
详细请看教程
[JCenter已经提桶跑路，是时候学会上传到Maven Central了](https://mp.weixin.qq.com/s/CrfYc1KsugJKPy_0rDZ49Q)