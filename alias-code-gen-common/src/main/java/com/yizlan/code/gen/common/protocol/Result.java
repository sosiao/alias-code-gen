/*
 * Copyright (C) 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yizlan.code.gen.common.protocol;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yizlan.gelato.canonical.protocol.TerResult;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Global response result
 *
 * @author Zen Gershon
 * @since 1.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> implements TerResult<String, String, T>, Serializable {
    private static final long serialVersionUID = 1L;

    // 返回是否成功
    private Boolean success = false;

    // 返回信息
    @JsonProperty("msg")
    private String message = "操作成功";

    // 错误码
    private String code;

    private T data;

    public Result(Boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    /**
     * 构建返回结果
     *
     * @param success
     * @param msg
     * @param code
     * @param data
     * @return
     */
    private static <T> Result<T> build(Boolean success, String msg, String code, T data) {
        return new Result<>(success, msg, code, data);
    }

    /**
     * 构建成功结果
     *
     * @param msg
     * @param data
     * @return
     */
    public static <T> Result<T> success(String msg, T data) {
        return build(Boolean.TRUE, msg, null, data);
    }

    /**
     * 构建成功结果
     *
     * @param msg
     * @return
     */
    public static <T> Result<T> success(String msg) {
        return build(Boolean.TRUE, msg, null, null);
    }

    /**
     * 构建成功结果
     *
     * @param data
     * @return
     */
    public static <T> Result<T> defaultIfSuccess(T data) {
        return new Result<>(Boolean.TRUE, data);
    }

    /**
     * 构建成功结果
     *
     * @return
     */
    public static <T> Result<T> defaultIfSuccess() {
        return new Result<>(Boolean.TRUE, null);
    }

    /**
     * 构建失败结果
     *
     * @param code
     * @param msg
     * @return
     */
    public static <T> Result<T> failure(String code, String msg) {
        return build(Boolean.FALSE, msg, code, null);
    }

    @Override
    public Result<T> success() {
        return new Result<>(Boolean.TRUE, null);
    }

    @Override
    public Result<T> failure() {
        return build(Boolean.FALSE, null, null, null);
    }
}
