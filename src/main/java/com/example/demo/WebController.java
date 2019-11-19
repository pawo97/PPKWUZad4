package com.example.demo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebController {

    @GetMapping("/employees/{name}")
    public String getPeople(@PathVariable String name) throws Exception {
        Document document = Jsoup.parse(sendGet(name));

        Elements people = document.select("div.user-profile-container");
        String content = "";

        for (Element p : people) {
            content += p.toString();
        }
        return content;
    }

    private static String sendGet(String name) throws Exception {

        String url = "https://adm.edu.p.lodz.pl/user/users.php?search=" + name + "&x=0&y=0";
        System.out.println(url);

        HttpURLConnection httpClient = (HttpURLConnection) new URL(url).openConnection();

        httpClient.setRequestMethod("GET");
        httpClient.setRequestProperty("User-Agent", "Mozilla/5.0");

        int responseCode = httpClient.getResponseCode();

        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        try (BufferedReader in = new BufferedReader(new InputStreamReader(httpClient.getInputStream()))) {

            StringBuilder response = new StringBuilder();
            String line;

            while ((line = in.readLine()) != null) {
                response.append(line);
            }

            System.out.println(response.toString());
            return response.toString();
        }

    }
}
