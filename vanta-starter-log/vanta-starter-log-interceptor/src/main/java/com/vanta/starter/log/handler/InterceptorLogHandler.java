package com.vanta.starter.log.handler;


/**
 * MVC 拦截器模式使用的默认日志处理器。
 * <p>
 * 当前类不新增状态，只复用 {@link AbstractLogHandler} 的通用采集规则，便于自动配置按接入模式区分 Bean 类型。
 * 业务方需要改变采集规则时，可以提供自己的同类型 Bean 覆盖默认实现。
 * </p>
 */
public class InterceptorLogHandler extends AbstractLogHandler {
}
