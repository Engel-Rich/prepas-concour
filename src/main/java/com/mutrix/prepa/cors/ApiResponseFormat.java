package com.mutrix.prepa.cors;

import lombok.*;
import org.springframework.data.domain.Page;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ApiResponseFormat<T> {
    private Integer statusCode;
    private Boolean success;
    private String error;
    private T data;

    public static <T> ApiResponseFormat<T> fromError(String error, Integer statusCode) {
        return ApiResponseFormat.<T>builder()
                .error(error).statusCode(statusCode).success(false).data(null)
                .build();
    }
    public static <T>ApiResponseFormat<T> fromError(String error) {
        return ApiResponseFormat.<T>builder()
                .error(error).statusCode(400).success(false).data(null)
                .build();
    }
    public static  <T>ApiResponseFormat<T> fromResponse(T data) {
        return ApiResponseFormat.<T>builder()
                .error(null).statusCode(200).success(true).data(data)
                .build();
    }
    public static  <T>ApiResponseFormat<T> fromResponseCreate(T data) {
        return ApiResponseFormat.<T>builder()
                .error(null).statusCode(201).success(true).data(data)
                .build();
    }
    public static  <T>ApiResponseFormat<T> fromResponse(T data, Integer statusCode) {
        return ApiResponseFormat.<T>builder()
                .error(null).statusCode(statusCode).success(true).data(data)
                .build();
    }

    public static <T> ApiResponseFormat<PageResponse<T>> fromPage(Page<T> page) {

        PageResponse<T> pageResponse = PageResponse.<T>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();

        return ApiResponseFormat.<PageResponse<T>>builder()
                .data(pageResponse)
                .statusCode(200)
                .success(true)
                .build();
    }
}
