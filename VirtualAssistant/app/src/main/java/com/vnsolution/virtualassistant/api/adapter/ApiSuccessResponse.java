package com.vnsolution.virtualassistant.api.adapter;

public class ApiSuccessResponse<T> extends ApiResponse<T> {
    private T body;

    public ApiSuccessResponse(T body) {
        super();
        this.body = body;
    }

    public T getBody() {
        return body;
    }
}
