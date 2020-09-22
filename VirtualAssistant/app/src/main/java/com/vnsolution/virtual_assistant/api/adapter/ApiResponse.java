package com.vnsolution.virtual_assistant.api.adapter;

import java.io.IOException;

import retrofit2.Response;

public abstract class ApiResponse<T> {
    public static <T> ApiResponse<T> create(Response<T> response) throws IOException {
        if (response.isSuccessful()) {
            T body = response.body();
            if (body == null || response.code() == 204) {
                return new ApiEmptyResponse();
            } else {
                return new ApiSuccessResponse(body);
            }
        } else {
            String errorString = ((response.errorBody() != null) && (response.errorBody().string() != null)) ? response.errorBody().string() : response.message();
            return new ApiErrorResponse(response.code(), errorString);
        }
    }

    public static <T> ApiErrorResponse<T> create(int errorCode, Throwable error) {
        String errorMessage = (error != null && error.getMessage() != null && !error.getMessage().isEmpty()) ? error.getMessage() : "Unknown Error!";
        return new ApiErrorResponse(errorCode, errorMessage);
    }
}
