package com.vnsolution.virtualassistant.di;

import android.app.Application;

import androidx.room.Room;

import com.vnsolution.virtualassistant.api.GoogleApi;
import com.vnsolution.virtualassistant.api.WeatherApi;
import com.vnsolution.virtualassistant.api.adapter.LiveDataCallAdapter;
import com.vnsolution.virtualassistant.api.adapter.LiveDataCallAdapterFactory;
import com.vnsolution.virtualassistant.common.Constants;
import com.vnsolution.virtualassistant.db.VirtualAssistantDb;
import com.vnsolution.virtualassistant.db.dao.CallHistoryDao;
import com.vnsolution.virtualassistant.db.dao.PhoneAppDao;
import com.vnsolution.virtualassistant.db.dao.PhoneContactDao;
import com.vnsolution.virtualassistant.db.dao.PlaceDao;
import com.vnsolution.virtualassistant.db.dao.PlaceHistoryDao;
import com.vnsolution.virtualassistant.db.dao.SettingDao;
import com.vnsolution.virtualassistant.db.dao.SmsHistoryDao;
import com.vnsolution.virtualassistant.ui.places.v2.VisitedPlacesV2Activity;
import com.vnsolution.virtualassistant.ui.places.v2.VisitedPlacesV2ViewPagerAdapter;

import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(includes = ViewModelModule.class)
public class AppModule {
    @Singleton
    @Provides
    public WeatherApi provideWeatherService() {
        // https://stackoverflow.com/questions/32948083/is-there-a-way-to-add-query-parameter-to-every-request-with-retrofit-2
        // Add query params to each request.
        // Issue: https://stackoverflow.com/questions/34674820/unsupported-operation-android-retrofit-okhttp-adding-interceptor-in-okhttpcl
        // Retrofit 2.0-beta2
//        OkHttpClient client = new OkHttpClient();
//        client.interceptors().add(new Interceptor() {
//            @Override
//            public Response intercept(Chain chain) throws IOException {
//                Request request = chain.request();
//                HttpUrl url = request.url().newBuilder()
//                        .addQueryParameter("units", "metric")
//                        .addQueryParameter("appid", openWeatherKey)
//                        .build();
//                request = request.newBuilder().url(url).build();
//                return chain.proceed(request);
//            }
//        });
        // Retrofit 2.0-beta3
        // We have to use builder if you want to create OkHttpClient object.
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(
                        new Interceptor() {
                            @Override
                            public Response intercept(Chain chain) throws IOException {
                                Request request = chain.request();
                                HttpUrl url = request.url().newBuilder()
                                        .addQueryParameter("units", "metric")
                                        .addQueryParameter("appid", Constants.OPEN_WEATHER_API_KEY)
                                        .build();
                                request = request.newBuilder().url(url).build();
                                return chain.proceed(request);
                            }
                        })
                .build();

        return new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create())
//                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .client(okHttpClient)
                .build()
                .create(WeatherApi.class);
    }

    @Singleton
    @Provides
    public GoogleApi provideGoogleService() {
        // https://stackoverflow.com/questions/32948083/is-there-a-way-to-add-query-parameter-to-every-request-with-retrofit-2
        // Add query params to each request.
        // Issue: https://stackoverflow.com/questions/34674820/unsupported-operation-android-retrofit-okhttp-adding-interceptor-in-okhttpcl
        // Retrofit 2.0-beta2
//        OkHttpClient client = new OkHttpClient();
//        client.interceptors().add(new Interceptor() {
//            @Override
//            public Response intercept(Chain chain) throws IOException {
//                Request request = chain.request();
//                HttpUrl url = request.url().newBuilder()
//                        .addQueryParameter("key", Constants.GM_API_KEY)
//                        .build();
//                request = request.newBuilder().url(url).build();
//                return chain.proceed(request);
//            }
//        });
        // Retrofit 2.0-beta3
        // We have to use builder if you want to create OkHttpClient object.
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(
                        new Interceptor() {
                            @Override
                            public Response intercept(Chain chain) throws IOException {
                                Request request = chain.request();
                                HttpUrl url = request.url().newBuilder()
                                        .addQueryParameter("key", Constants.GM_API_KEY)
                                        .build();
                                request = request.newBuilder().url(url).build();
                                return chain.proceed(request);
                            }
                        })
                .build();

        return new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
//                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .client(okHttpClient)
                .build()
                .create(GoogleApi.class);
    }

    @Singleton
    @Provides
    public VirtualAssistantDb provideDb(Application app) {
        return Room
                .databaseBuilder(app, VirtualAssistantDb.class, "virtual_assistant.db")
                .createFromAsset("database/virtual_assistant.db")
                // https://medium.com/androiddevelopers/understanding-migrations-with-room-f01e04b07929
                // If you don’t want to provide migrations and you specifically want your database to be cleared when you upgrade the version, call fallbackToDestructiveMigration in the database builder
//                .fallbackToDestructiveMigration()
                // https://stackoverflow.com/questions/44167111/android-room-simple-select-query-cannot-access-database-on-the-main-thread
                // It's not recommended but you can access to database on main thread with allowMainThreadQueries()
                // Cannot access database on the main thread since it may potentially lock the UI for a long period of time.
                //.allowMainThreadQueries()
                .build();
    }

    @Singleton
    @Provides
    public PlaceDao providePlaceDao(VirtualAssistantDb db) {
        return db.placeDao();
    }

    @Singleton
    @Provides
    public PlaceHistoryDao providePlaceHistoryDao(VirtualAssistantDb db) { return db.placeHistoryDao(); }

    @Singleton
    @Provides
    public CallHistoryDao provideCallHistoryDao(VirtualAssistantDb db) { return db.callHistoryDao(); }

    @Singleton
    @Provides
    public SmsHistoryDao provideSmsHistoryDao(VirtualAssistantDb db) { return db.smsHistoryDao(); }

    @Singleton
    @Provides
    public PhoneContactDao providePhoneContactDao(VirtualAssistantDb db) { return db.phoneContactDao(); }

    @Singleton
    @Provides
    public PhoneAppDao providePhoneAppDao(VirtualAssistantDb db) { return db.phoneAppDao(); }

    @Singleton
    @Provides
    public SettingDao provideSettingDao(VirtualAssistantDb db) {
        return db.settingDao();
    }
}
