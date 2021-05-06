package com.haolin.select.city.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.haolin.select.city.R;
import com.haolin.select.city.bean.City;
import com.haolin.select.city.bean.PinnedBean;
import com.haolin.select.city.utils.LocateState;
import com.haolin.select.city.utils.PinyinUtils;
import com.haolin.select.city.view.PinnedSectionListView;
import com.haolin.select.city.view.WrapHeightGridView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 */
public class CityListAdapter extends BaseAdapter implements PinnedSectionListView.PinnedSectionListAdapter {
    private static final int VIEW_TYPE_COUNT = 4;

    private Context mContext;
    private LayoutInflater inflater;
    private List<City> mCities;
    private List<PinnedBean> mPinnedBeans;
    private HashMap<String, Integer> letterIndexes;
    //    private String[] sections;
    private List<String> lists;
    private OnCityClickListener onCityClickListener;
    private int locateState = LocateState.LOCATING;
    private String locatedCity;
    private List<String> cityIds = new ArrayList<>();
    private String cityId;
    public CityListAdapter(Context mContext) {
        this.mContext = mContext;
        this.inflater = LayoutInflater.from(mContext);
        cityIds.add("131");
        cityIds.add("289");
        cityIds.add("257");
        cityIds.add("340");
        cityIds.add("179");
        cityIds.add("315");
        cityIds.add("332");
        cityIds.add("218");
        cityIds.add("132");

    }

    public void addListCities(List<City> mCities){
        this.mCities = mCities;
        if (mCities == null) {
            mCities = new ArrayList<>();
        }
        mCities.add(0, new City(mContext.getString(R.string.location), "0", "0"));  //se
        mCities.add(1, new City(mContext.getString(R.string.location), "0", "0"));  //loca
        mCities.add(2, new City(mContext.getString(R.string.hot), "1", "1"));  //se
        mCities.add(3, new City(mContext.getString(R.string.hot), "1", "1"));  //hot
        int size = mCities.size();
        letterIndexes = new HashMap<>();
        lists = new ArrayList<>();
//        sections = new String[size];
        mPinnedBeans = new ArrayList<>();
        for (int index = 0; index < size; index++) {
            //当前城市拼音首字母
            String currentLetter = PinyinUtils.getFirstLetter(mCities.get(index).getNamePinyin());
//            String cityId = PinyinUtils.getFirstLetter(mCities.get(index).getCityid());
            //上个首字母，如果不存在设为""
            String previousLetter = index >= 1 ? PinyinUtils.getFirstLetter(mCities.get(index - 1).getNamePinyin()) : "";

            if (!TextUtils.equals(currentLetter, previousLetter)) {
                letterIndexes.put(currentLetter, index);
//                sections[index] = currentLetter;
                mPinnedBeans.add(new PinnedBean(PinnedBean.SECTION, currentLetter, mCities.get(index).getCityid()));
                if (index > 3) {
                    mPinnedBeans.add(new PinnedBean(PinnedBean.ITEM, mCities.get(index).getName(), mCities.get(index).getCityid()));
                    lists.add(currentLetter);
                }

            } else {
                if (currentLetter.equals(mContext.getString(R.string.location))) {
                    mPinnedBeans.add(new PinnedBean(PinnedBean.LOCATE, mCities.get(index).getName(), mCities.get(index).getCityid()));
                } else if (currentLetter.equals(mContext.getString(R.string.hot))) {
                    mPinnedBeans.add(new PinnedBean(PinnedBean.HOT, mCities.get(index).getName(), mCities.get(index).getCityid()));
                } else {
                    mPinnedBeans.add(new PinnedBean(PinnedBean.ITEM, mCities.get(index).getName(), mCities.get(index).getCityid()));
                }

            }
        }
    }
    /**
     * 更新定位状态
     *
     * @param state
     */
    public void updateLocateState(int state, String city,String cityId) {
        this.locateState = state;
        this.locatedCity = city;
        this.cityId = cityId;
        notifyDataSetChanged();
    }

    /**
     * 获取字母索引的位置
     *
     * @param letter
     * @return
     */
    public int getLetterPosition(String letter) {
        Integer integer = letterIndexes.get(letter);
        if (integer == null)
            return -1;
        if (getIndex(letter) == -1)
            return integer;

        return integer + getIndex(letter);
    }

    public int getIndex(String letter) {

        return lists.indexOf(letter);
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        return mPinnedBeans.get(position).getType();
    }

    @Override
    public int getCount() {
        return mPinnedBeans == null ? 0 : mPinnedBeans.size();
    }

    @Override
    public PinnedBean getItem(int position) {
        return mPinnedBeans == null ? null : mPinnedBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        CityViewHolder holder = null;
        SelectHolder selectHolder = null;
        LocateHolder locateHolder = null;
        HotHolder hotHolder = null;
        int viewType = getItemViewType(position);
        if (view == null) {
            if (viewType == PinnedBean.SECTION) {
                view = inflater.inflate(R.layout.item_select, parent, false);
                selectHolder = new SelectHolder();
                selectHolder.select = (TextView) view.findViewById(R.id.select);
                view.setTag(selectHolder);
            } else if (viewType == PinnedBean.ITEM) {
                view = inflater.inflate(R.layout.item_city_listview, parent, false);
                holder = new CityViewHolder();
                holder.name = (TextView) view.findViewById(R.id.tv_item_city_listview_name);
                view.setTag(holder);
            } else if (viewType == PinnedBean.LOCATE) {
                view = inflater.inflate(R.layout.view_locate_city, parent, false);
                locateHolder = new LocateHolder();
                locateHolder.mImageView = (ImageView) view.findViewById(R.id.image);
                locateHolder.mProgressBar = (ProgressBar) view.findViewById(R.id.city_locating_progress);
                locateHolder.container = (ViewGroup) view.findViewById(R.id.layout_locate);
                locateHolder.stateView = (TextView) view.findViewById(R.id.tv_located_city);
                view.setTag(locateHolder);
            } else if (viewType == PinnedBean.HOT) {
                view = inflater.inflate(R.layout.view_hot_city, parent, false);
                hotHolder = new HotHolder();
                hotHolder.mGridView = (WrapHeightGridView) view.findViewById(R.id.gridview_hot_city);
                view.setTag(hotHolder);
            }

        } else {
            if (viewType == PinnedBean.SECTION) {
                selectHolder = (SelectHolder) view.getTag();
            } else if (viewType == PinnedBean.ITEM) {
                holder = (CityViewHolder) view.getTag();
            } else if (viewType == PinnedBean.LOCATE) {
                locateHolder = (LocateHolder) view.getTag();
            } else if (viewType == PinnedBean.HOT) {
                hotHolder = (HotHolder) view.getTag();
            }
        }

        if (viewType == PinnedBean.SECTION) {
            selectHolder.select.setText(mPinnedBeans.get(position).getText());
        } else if (viewType == PinnedBean.ITEM) {
            holder.name.setText(mPinnedBeans.get(position).getText());
            holder.name.setOnClickListener(v -> {
                if (onCityClickListener != null) {
                    onCityClickListener.onCityClick(mPinnedBeans.get(position).getText(), mPinnedBeans.get(position).getCityId());
                }
            });
        } else if (viewType == PinnedBean.LOCATE) {

            switch (locateState) {
                case LocateState.LOCATING:
                    locateHolder.mProgressBar.setVisibility(View.VISIBLE);
                    locateHolder.mImageView.setVisibility(View.GONE);
                    locateHolder.stateView.setText(mContext.getString(R.string.locating));
                    break;
                case LocateState.FAILED:
                    locateHolder.mProgressBar.setVisibility(View.GONE);
                    locateHolder.mImageView.setVisibility(View.VISIBLE);
                    locateHolder.stateView.setText(R.string.located_failed);
                    break;
                case LocateState.SUCCESS:
                    locateHolder.mProgressBar.setVisibility(View.GONE);
                    locateHolder.mImageView.setVisibility(View.VISIBLE);
                    locateHolder.stateView.setText(locatedCity);
                    break;
            }

            locateHolder.container.setOnClickListener(v -> {
                if (locateState == LocateState.FAILED) {
                    //重新定位
                    if (onCityClickListener != null) {
                        onCityClickListener.onLocateClick();
                    }
                } else if (locateState == LocateState.SUCCESS) {
                    //返回定位城市
                    if (onCityClickListener != null) {
                        onCityClickListener.onCityClick(locatedCity, cityId);
                    }
                }
            });

        } else if (viewType == PinnedBean.HOT) {

            final HotCityGridAdapter hotCityGridAdapter = new HotCityGridAdapter(mContext);
            hotHolder.mGridView.setAdapter(hotCityGridAdapter);
            hotHolder.mGridView.setOnItemClickListener((parent1, view1, position1, id) -> {
                if (onCityClickListener != null) {
                    onCityClickListener.onCityClick(hotCityGridAdapter.getItem(position1), cityIds.get(position1));
                }
            });
        }

        return view;
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType == PinnedBean.SECTION;
    }

    public static class CityViewHolder {
        TextView letter;
        TextView name;
    }

    public static class SelectHolder {
        TextView select;
    }

    public static class LocateHolder {
        TextView stateView;
        ViewGroup container;
        ProgressBar mProgressBar;
        ImageView mImageView;
    }

    public static class HotHolder {
        WrapHeightGridView mGridView;
    }


    public void setOnCityClickListener(OnCityClickListener listener) {
        this.onCityClickListener = listener;
    }

    public interface OnCityClickListener {
        void onCityClick(String name, String cityId);

        void onLocateClick();
    }
}
