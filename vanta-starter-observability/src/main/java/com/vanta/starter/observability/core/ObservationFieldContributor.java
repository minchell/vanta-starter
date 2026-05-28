package com.vanta.starter.observability.core;

import java.util.Map;

/**
 * 观测字段贡献器。
 *
 * <p>业务可以替换该接口，把当前上下文转换为日志字段、指标 tag 或链路属性。</p>
 */
@FunctionalInterface
public interface ObservationFieldContributor {

    /**
     * 提取需要输出的观测字段。
     *
     * @param context 当前观测上下文
     * @return 字段 Map
     */
    Map<String, String> contribute(ObservationContext context);
}
