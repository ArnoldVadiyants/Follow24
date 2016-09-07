package com.newstee.model.data;

/**
 * Created by Arnold on 06.09.2016.
 */
public class IpLab {
    private String countryCode = "";
    private static IpLab sIpLab;
    private IpLab() {
    }
    public static IpLab getInstance() {
        if (sIpLab == null) {
            sIpLab = new IpLab();
        }
        return sIpLab;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }



}