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
    private RedisService redisService;

    public WeatherResponse getWeather(String city) {
        WeatherResponse cached = redisService.get("weather_of_" + city, WeatherResponse.class);
        if (cached != null)
            return cached;

        String weatherApiUrl = appCache.appCache.get(AppCache.keys.WEATHER_API.toString());
        if (weatherApiUrl == null || weatherApiUrl.isEmpty() || apiKey == null || apiKey.isEmpty())
            return null;

        weatherApiUrl = weatherApiUrl
                .replace(Placeholders.CITY, city)
                .replace(Placeholders.API_KEY, apiKey);

        ResponseEntity<WeatherResponse> response = restTemplate.exchange(
                weatherApiUrl, HttpMethod.GET, null, WeatherResponse.class);
        WeatherResponse body = response.getBody();

        if (body != null)
            redisService.set("weather_of_" + city, body, 300L);

        return body;
    }
}
