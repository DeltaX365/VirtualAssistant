package com.vnsolution.virtual_assistant.service.weather;

public class WeatherManager {
    private static WeatherManager instance;

    private WeatherManager(){}

    public static synchronized WeatherManager getInstance() {
        if (instance == null) {
            instance = new WeatherManager();
        }
        return instance;
    }
}
