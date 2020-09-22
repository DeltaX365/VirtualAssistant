package com.vnsolution.virtual_assistant.api.adapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import retrofit2.CallAdapter;
import retrofit2.Retrofit;

public class LiveDataCallAdapterFactory extends CallAdapter.Factory {
    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        if (!(returnType instanceof ParameterizedType)) {
            throw new IllegalArgumentException("resource must be parameterized");
        }
        Type observableType = CallAdapter.Factory.getParameterUpperBound(0, (ParameterizedType) returnType);
        if (!(observableType instanceof ParameterizedType)) {
            throw new IllegalArgumentException("resource must be parameterized");
        }
        return new LiveDataCallAdapter<Object>(CallAdapter.Factory.getParameterUpperBound(0, (ParameterizedType) observableType));
    }
}
