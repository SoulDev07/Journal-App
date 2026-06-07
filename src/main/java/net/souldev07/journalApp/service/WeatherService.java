package net.souldev07.journalApp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import net.souldev07.journalApp.api.response.WeatherResponse;
import net.souldev07.journalApp.cache.AppCache;
import net.souldev07.journalApp.constants.Placeholders;

@Service
public class WeatherService {

    @Value("${apikey.weather}")
    private String apiKey;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AppCache appCache;

    public WeatherResponse getWeather(String city) {
        String weatherApiUrl = appCache.appCache.get(AppCache.keys.WEATHER_API.toString())
                .replace(Placeholders.CITY, city)
                .replace(Placeholders.API_KEY, apiKey);

        ResponseEntity<WeatherResponse> response = restTemplate.exchange(weatherApiUrl, HttpMethod.GET, null,
                WeatherResponse.class);
        WeatherResponse weatherData = response.getBody();

        return weatherData;
    }
}
