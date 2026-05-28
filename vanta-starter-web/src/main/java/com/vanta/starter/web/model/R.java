package com.vanta.starter.web.model;

import cn.hutool.extra.spring.SpringUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.feiniaojin.gracefulresponse.api.ResponseStatusFactory;
import com.feiniaojin.gracefulresponse.data.Response;
import com.feiniaojin.gracefulresponse.data.ResponseStatus;
import com.feiniaojin.gracefulresponse.defaults.DefaultResponseStatus;
import com.feiniaojin.gracefulresponse.defaults.DefaultResponseStatusFactoryImpl;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;

/**
 * 统一接口响应模型。
 * <p>
 * 该响应对象适配 graceful-response 的 {@link Response} 契约，同时提供 Vanta 常用的
 * {@code code/msg/success/timestamp/data} 响应字段，便于前端和 OpenAPI 文档形成稳定约定。
 * </p>
 *
 * @param <T> 响应数据类型。
 */
@Schema(description = "响应信息")
public class R<T> implements Response {

    /**
     * 响应状态工厂。
     * <p>
     * 从 Spring 容器获取，用于读取 graceful-response 配置中的成功和失败默认状态。
     * </p>
     */
    private static final ResponseStatusFactory RESPONSE_STATUS_FACTORY = loadResponseStatusFactory();

    /**
     * 状态码。
     * <p>
     * 字段用于 OpenAPI 结构展示，实际读取和写入会委托到 {@link #status}。
     * </p>
     */
    @Schema(description = "状态码", example = "0")
    private String code;

    /**
     * 状态信息。
     * <p>
     * 字段用于 OpenAPI 结构展示，实际读取和写入会委托到 {@link #status}。
     * </p>
     */
    @Schema(description = "状态信息", example = "ok")
    private String msg;

    /**
     * 是否成功。
     * <p>
     * 字段用于 OpenAPI 结构展示，实际返回值由 {@link #isSuccess()} 根据状态码动态判断。
     * </p>
     */
    @Schema(description = "是否成功", example = "true")
    private boolean success;

    /**
     * 时间戳。
     * <p>
     * 字段用于 OpenAPI 结构展示，实际返回值由 {@link #getTimestamp()} 动态生成。
     * </p>
     */
    @Schema(description = "时间戳", example = "1691453288000")
    private Long timestamp;

    /**
     * 响应数据。
     */
    @Schema(description = "响应数据")
    private T data;

    /**
     * graceful-response 状态信息。
     * <p>
     * code 和 msg 的真实来源，默认使用 graceful-response 的默认状态实现。
     * </p>
     */
    private ResponseStatus status = new DefaultResponseStatus();

    /**
     * 创建空响应对象。
     * <p>
     * 主要供 Jackson、OpenAPI 或框架反射实例化使用。
     * </p>
     */
    public R() {
    }

    /**
     * 使用响应状态创建响应对象。
     *
     * @param status 响应状态。
     */
    public R(ResponseStatus status) {
        this.status = status;
    }

    /**
     * 使用状态码和状态信息创建响应对象。
     *
     * @param code 业务状态码。
     * @param msg  业务状态信息。
     */
    public R(String code, String msg) {
        this.setCode(code);
        this.setMsg(msg);
    }

    /**
     * 使用响应状态和响应数据创建响应对象。
     *
     * @param status 响应状态。
     * @param data   响应数据。
     */
    public R(ResponseStatus status, T data) {
        this(status);
        this.setData(data);
    }

    /**
     * 使用状态码、状态信息和响应数据创建响应对象。
     *
     * @param code 业务状态码。
     * @param msg  业务状态信息。
     * @param data 响应数据。
     */
    public R(String code, String msg, T data) {
        this(code, msg);
        this.setData(data);
    }

    private static ResponseStatusFactory loadResponseStatusFactory() {
        try {
            return SpringUtil.getBean(ResponseStatusFactory.class);
        } catch (RuntimeException ex) {
            return new DefaultResponseStatusFactoryImpl();
        }
    }

    /**
     * 操作成功
     *
     * @return 默认成功响应。
     */
    public static R ok() {
        return new R(RESPONSE_STATUS_FACTORY.defaultSuccess());
    }

    /**
     * 操作成功
     *
     * @param data 响应数据
     * @return 携带数据的默认成功响应。
     */
    public static <T> R<T> ok(T data) {
        return new R<>(RESPONSE_STATUS_FACTORY.defaultSuccess(), data);
    }

    /**
     * 操作成功
     *
     * @param msg  业务状态信息
     * @param data 响应数据
     * @return 携带自定义成功消息和数据的响应。
     */
    public static R ok(String msg, Object data) {
        R r = ok(data);
        r.setMsg(msg);
        return r;
    }

    /**
     * 操作失败
     *
     * @return 默认失败响应。
     */
    public static R fail() {
        return new R(RESPONSE_STATUS_FACTORY.defaultError());
    }


    /**
     * 操作失败
     *
     * @param code 业务状态码
     * @param msg  业务状态信息
     * @return 携带自定义状态码和状态信息的失败响应。
     */
    public static R fail(String code, String msg) {
        return new R(code, msg);
    }

    /**
     * 获取 graceful-response 状态对象。
     *
     * @return 响应状态对象。
     */
    @Override
    @JsonIgnore
    public ResponseStatus getStatus() {
        return status;
    }

    /**
     * 设置 graceful-response 状态对象。
     *
     * @param status 响应状态对象。
     */
    @Override
    public void setStatus(ResponseStatus status) {
        this.status = status;
    }

    /**
     * 获取 graceful-response 载荷对象。
     *
     * @return 响应数据。
     */
    @Override
    @JsonIgnore
    public Object getPayload() {
        return data;
    }

    /**
     * 设置 graceful-response 载荷对象。
     *
     * @param payload 响应数据。
     */
    @SuppressWarnings("unchecked")
    @Override
    public void setPayload(Object payload) {
        this.data = (T) payload;
    }

    /**
     * 获取业务状态码。
     *
     * @return 业务状态码。
     */
    public String getCode() {
        return status.getCode();
    }

    /**
     * 设置业务状态码。
     *
     * @param code 业务状态码。
     */
    public void setCode(String code) {
        status.setCode(code);
    }

    /**
     * 获取业务状态信息。
     *
     * @return 业务状态信息。
     */
    public String getMsg() {
        return status.getMsg();
    }

    /**
     * 设置业务状态信息。
     *
     * @param msg 业务状态信息。
     */
    public void setMsg(String msg) {
        status.setMsg(msg);
    }

    /**
     * 获取响应数据。
     *
     * @return 响应数据。
     */
    public T getData() {
        return data;
    }

    /**
     * 设置响应数据。
     *
     * @param data 响应数据。
     */
    public void setData(T data) {
        this.data = data;
    }

    /**
     * 判断当前响应是否为成功响应。
     *
     * @return 当前状态码等于默认成功状态码时返回 {@code true}。
     */
    public boolean isSuccess() {
        return Objects.equals(RESPONSE_STATUS_FACTORY.defaultSuccess().getCode(), status.getCode());
    }

    /**
     * 获取响应生成时间戳。
     *
     * @return 当前系统毫秒时间戳。
     */
    public Long getTimestamp() {
        return System.currentTimeMillis();
    }
}
