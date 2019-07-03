package com.example.reptile;

import lombok.Data;

import java.util.List;

/**
 * @author peter
 * date: 2019-07-02 17:38
 **/
@Data
public class CarBrand {

    private String brandName;

    private String iconUrl;

    private List<String> series;

    public CarBrand(String brandName, String iconUrl) {
        this.brandName = brandName;
        this.iconUrl = iconUrl;
    }
}
