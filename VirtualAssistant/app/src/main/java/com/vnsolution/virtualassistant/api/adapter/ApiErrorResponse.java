package com.vnsolution.virtualassistant.api.adapter;

public class ApiErrorResponse<T> extends ApiResponse<T> {
    private int errorCode;
    private String errorMessage;

    public ApiErrorResponse(int errorCode, String errorMessage) {
        super();
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
