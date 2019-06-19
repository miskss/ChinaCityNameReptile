package com.example.reptile;

import lombok.Data;

import java.util.List;

/**
 * @author peter
 * date: 2019-06-19 09:38
 **/
@Data
public class Province {

    private String code;


    private String name;



    private List<City> cities;


    public Province (String name) {
        this.name = name;
    }
}
