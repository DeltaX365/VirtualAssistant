package com.vnsolution.virtualassistant.di;

import com.vnsolution.virtualassistant.api.GoogleService;
import com.vnsolution.virtualassistant.api.WeatherService;

import java.io.IOException;

import javax.inject.Singleton;

import dagger.Provides;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppModule {
    @Singleton
    @Provides
    public WeatherService provideWeatherService() {
        final String openWeatherKey = "071dcc0541ea2d9dfaef941b9a414685";

        // https://stackoverflow.com/questions/32948083/is-there-a-way-to-add-query-parameter-to-every-request-with-retrofit-2
        // Add query params to each request.
        OkHttpClient client = new OkHttpClient();
        client.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                HttpUrl url = request.url().newBuilder()
                        .addQueryParameter("units", "metric")
                        .addQueryParameter("appid", openWeatherKey)
                        .build();
                request = request.newBuilder().url(url).build();
                return chain.proceed(request);
            }
        });

        return new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(WeatherService.class);
    }

    @Singleton
    @Provides
    public GoogleService provideGoogleService() {
        final String googleServiceKey = "AIzaSyC498VPSvURSXxlF22TFDUIpF_TC2GFQZ0";

        // https://stackoverflow.com/questions/32948083/is-there-a-way-to-add-query-parameter-to-every-request-with-retrofit-2
        // Add query params to each request.
        OkHttpClient client = new OkHttpClient();
        client.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                HttpUrl url = request.url().newBuilder()
                        .addQueryParameter("key", googleServiceKey)
                        .build();
                request = request.newBuilder().url(url).build();
                return chain.proceed(request);
            }
        });

        return new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GoogleService.class);
    }
}
