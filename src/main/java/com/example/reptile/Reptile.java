package com.example.reptile;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author peter
 * date: 2019-06-19 08:51
 **/
public class Reptile {

    private static final String BASE_URL = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2018/";
    private static RestTemplate restTemplate;

    static {
        restTemplate = new RestTemplate();
        //页面的编码为gb2312
        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(Charset.forName("gb2312")));
    }

    public static void main(String[] args) throws IOException {

        String url = BASE_URL + "index.html";


        Map<String, String> provinceMap = getProvinceMap(url);

        List<Province> provinces = provinceMap.entrySet().stream().map(entry -> {

            String provinceUrl = BASE_URL + entry.getKey();

            Elements citytrs = getBodyElements(provinceUrl).getElementsByClass("citytr");

            Province province = new Province(entry.getValue());

            List<City> cities = citytrs.stream()
                    .map(tr -> {

                        String code = tr.child(0).child(0).ownText();

                        String name = tr.child(1).child(0).ownText();

                        if ("市辖区".equals(name)) {
                            province.setCode(code);
                            return null;
                        } else if ("县".equals(name) || "省直辖县级行政区划".equals(name)) {
                            return null;
                        } else {
                            return new City(code, name);
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            province.setCities(cities);
            return province;
        }).collect(Collectors.toList());
        Path file = Files.createFile(Paths.get("province.json"));
        Files.write(file, new ObjectMapper().writeValueAsString(provinces).getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 获取 省 的名称和url
     *
     * @param url
     * @return Map key：url,value: name
     */
    private static Map<String, String> getProvinceMap(String url) {
        Elements provincetr = getBodyElements(url).getElementsByClass("provincetr");
        Map<String, String> provinceMap = new HashMap<>();
        provincetr.forEach(element -> {

            Elements tagA = element.getElementsByTag("a");

            tagA.forEach(a -> {
                String provinceName = a.ownText();
                String provinceUrl = a.attr("href");
                provinceMap.put(provinceUrl, provinceName);
            });
        });

        return provinceMap;
    }

    private static Element getBodyElements(String url) {
        String forObject = restTemplate.getForObject(url, String.class);

        Assert.notNull(forObject, forObject);
        Document parse = Jsoup.parse(forObject);

        return parse.body();
    }


}
