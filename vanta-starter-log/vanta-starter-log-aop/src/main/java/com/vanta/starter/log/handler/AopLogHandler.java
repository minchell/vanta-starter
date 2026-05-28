package com.vanta.starter.log.handler;


/**
 * AOP 模式使用的默认日志处理器。
 * <p>
 * 当前类不新增状态，只复用 {@link AbstractLogHandler} 的通用采集规则，便于 AOP 自动配置拥有独立的可替换 Bean。
 * 业务方需要改变注解解析、采集字段或访问日志输出时，可以提供自己的同类型 Bean 覆盖默认实现。
 * </p>
 */
public class AopLogHandler extends AbstractLogHandler {
}
