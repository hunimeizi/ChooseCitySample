package com.haolin.select.city.bean;

public class City {
    private String cityid;
    private String name;
    private String namePinyin;

    public City(String name, String pinyin, String cityId) {
        this.name = name;
        this.namePinyin = pinyin;
        this.cityid = cityId;
    }

    public String getCityid() {
        return cityid;
    }

    public void setCityid(String cityid) {
        this.cityid = cityid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNamePinyin() {
        return namePinyin;
    }

    public void setNamePinyin(String namePinyin) {
        this.namePinyin = namePinyin;
    }

    @Override
    public String toString() {
        return "City{" +
                "cityid='" + cityid + '\'' +
                ", name='" + name + '\'' +
                ", namePinyin='" + namePinyin + '\'' +
                '}';
    }
}
