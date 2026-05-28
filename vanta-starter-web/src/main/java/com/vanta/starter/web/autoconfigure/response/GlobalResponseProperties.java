package com.vanta.starter.web.autoconfigure.response;

import com.feiniaojin.gracefulresponse.GracefulResponseProperties;
import com.vanta.starter.core.constant.PropertiesConstants;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 全局响应配置属性。
 * <p>
 * 该类继承 graceful-response 原生配置属性，并绑定到 Vanta Starter 的
 * {@code vanta-starter.web.response} 配置前缀，便于项目统一管理 starter 配置。
 * </p>
 */
@ConfigurationProperties(PropertiesConstants.WEB_RESPONSE)
public class GlobalResponseProperties extends GracefulResponseProperties {
}
