package com.example.reptile;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author peter
 * date: 2019-07-02 16:19
 **/
public class CarReptile {
    private final static String INDEX = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String BASE_URL = "https://www.autohome.com.cn/grade/carhtml/";

    private static ConcurrentHashMap<Character, List<CarBrand>> hashMap = new ConcurrentHashMap<>();


    public static void main(String[] args) throws IOException {
        for (int i = 0; i < INDEX.length(); i++) {

            char charAt = INDEX.charAt(i);

            String url = BASE_URL + charAt + ".html";


            StringBuilder sb = getHttp(url);

            Document parse = Jsoup.parse(sb.toString());
            Elements dl = parse.body().getElementsByTag("dl");
            if (dl.isEmpty()) {
                hashMap.put(charAt, new ArrayList<>());
                continue;
            }


            List<CarBrand> carBrands = dl.stream().map(element -> {
                Element dt = element.getElementsByTag("dt").get(0);

                String iconUrl = "https:" + dt.getElementsByTag("img").get(0).attr("src");
                String brandName = dt.child(1).child(0).ownText();
                CarBrand carBrand = new CarBrand(brandName, iconUrl);

                List<String> collect = element.child(1).getElementsByTag("li").stream().map(li -> {

                    Elements h4 = li.getElementsByTag("h4");
                    if (h4.isEmpty()) return null;
                    return h4.get(0).child(0).ownText();
                }).filter(Objects::nonNull).collect(Collectors.toList());


                carBrand.setSeries(collect);
                return carBrand;
            }).collect(Collectors.toList());

            hashMap.put(charAt, carBrands);
        }


        System.out.println(hashMap.values().stream().flatMap(Collection::stream).map(CarBrand::getSeries).mapToLong(Collection::size).sum());

        String jsonString = new ObjectMapper().writeValueAsString(hashMap);


        Path path = Files.createFile(Paths.get("car_list.json"));

        Files.deleteIfExists(path);

        Files.write(path, jsonString.getBytes(StandardCharsets.UTF_8));

    }

    private static StringBuilder getHttp(String httpAddress) throws IOException {
        URL url = new URL(httpAddress);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setDoOutput(true);
        urlConnection.setRequestProperty("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3");
        urlConnection.connect();

        InputStream inputStream = urlConnection.getInputStream();

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "gb2312"));

        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line);
        }
        urlConnection.disconnect();
        return sb;
    }


}
