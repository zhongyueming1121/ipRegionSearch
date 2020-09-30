package com.ymz.ip.model;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * IP地理位置信息
 *
 * @author ymz
 * @date 2020/04/22 11:47
 */
public class IPInfoDataVO implements Serializable {
    public IPInfoDataVO() {

    }

    public IPInfoDataVO(String country, String province, String city, String operators) {
        this.country = country;
        this.province = province;
        this.city = city;
        this.operators = operators;
    }

    /**
     * 国家
     */
    private String country;
    /**
     * 省/特区
     */
    private String province;
    /**
     * 市
     */
    private String city;

    private String operators;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getOperators() {
        return operators;
    }

    public void setOperators(String operators) {
        this.operators = operators;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", IPInfoDataVO.class.getSimpleName() + "[", "]")
                .add("country='" + country + "'")
                .add("province='" + province + "'")
                .add("city='" + city + "'")
                .add("operators='" + operators + "'")
                .toString();
    }
}
