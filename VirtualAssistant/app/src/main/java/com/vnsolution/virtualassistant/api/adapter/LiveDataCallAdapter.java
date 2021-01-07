package com.vnsolution.virtualassistant.api.adapter;

import androidx.lifecycle.LiveData;

import com.vnsolution.virtualassistant.common.AppSetting;

import java.io.IOException;
import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Response;

public class LiveDataCallAdapter<T> implements CallAdapter<T, LiveData<ApiResponse<T>>> {
    private Type responseType;

    public LiveDataCallAdapter(Type responseType) {
        this.responseType = responseType;
    }

    @Override
    public Type responseType() {
        return this.responseType;
    }

    @Override
    public LiveData<ApiResponse<T>> adapt(Call<T> call) {
        return new LiveData<ApiResponse<T>>() {
            private boolean isSuccess = false;

            @Override
            protected void onActive() {
                super.onActive();
                if (!isSuccess) enqueue();
            }

            @Override
            protected void onInactive() {
                super.onInactive();
                dequeue();
            }

            private void enqueue() {
                call.enqueue(new Callback<T>() {
                    @Override
                    public void onResponse(Call<T> call, Response<T> response) {
                        try {
                            postValue(ApiResponse.create(response));
                            isSuccess = true;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<T> call, Throwable t) {
                        postValue(ApiResponse.create(AppSetting.UNKNOWN_CODE, t));
                    }
                });
            }

            private void dequeue() {
                if (call.isExecuted()) call.cancel();
            }
        };
    }
}
