package com.example.reptile;

import lombok.Data;

/**
 * @author peter
 * date: 2019-06-19 09:38
 **/
@Data
public class City {

    private String code;

    private String name;

    public City(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
