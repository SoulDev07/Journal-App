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

    @Value("${weather.api.key}")
    private String apiKey;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AppCache appCache;

    @Autowired
    RedisService redisService;

    public WeatherResponse getWeather(String city) {
        WeatherResponse weatherResponse = redisService.get("weather:" + city, WeatherResponse.class);
        if (weatherResponse != null)
            return weatherResponse;

        String weatherApiUrl = appCache.appCache.get(AppCache.keys.WEATHER_API.toString());
        if (weatherApiUrl == null || weatherApiUrl.isEmpty() || apiKey == null || apiKey.isEmpty())
            return null;

        weatherApiUrl = weatherApiUrl
                .replace(Placeholders.CITY, city)
                .replace(Placeholders.API_KEY, apiKey);

        ResponseEntity<WeatherResponse> response = restTemplate.exchange(weatherApiUrl, HttpMethod.GET, null,
                WeatherResponse.class);
        weatherResponse = response.getBody();

        if (weatherResponse != null)
            redisService.set("weather:" + city, weatherResponse, 300L);

        return weatherResponse;
    }
}
