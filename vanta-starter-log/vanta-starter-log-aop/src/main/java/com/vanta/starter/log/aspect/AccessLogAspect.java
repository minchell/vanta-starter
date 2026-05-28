package com.vanta.starter.log.aspect;

import com.vanta.starter.log.handler.LogHandler;
import com.vanta.starter.log.http.servlet.RecordableServletHttpRequest;
import com.vanta.starter.log.http.servlet.RecordableServletHttpResponse;
import com.vanta.starter.log.model.AccessLogContext;
import com.vanta.starter.log.model.LogProperties;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;

/**
 * 基于 AOP 的访问日志切面。
 * <p>
 * 该切面面向 Spring Web 控制器映射注解，负责记录一次 HTTP 请求的开始、结束、状态码和耗时。
 * 它只输出本地日志，不默认访问外部中间件；业务方如果需要落库或上报指标，应替换 {@link LogHandler} 或组合其他 starter。
 * </p>
 */
@Aspect
public class AccessLogAspect {

    /**
     * 日志 starter 的配置属性。
     * <p>
     * 访问日志开关、排除路径、参数输出策略等都从这里读取。
     * </p>
     */
    private final LogProperties logProperties;
    /**
     * 访问日志生命周期处理器。
     * <p>
     * 负责真正的开始日志、结束日志输出，并维护请求级上下文。
     * </p>
     */
    private final LogHandler logHandler;

    /**
     * 创建访问日志切面。
     *
     * @param logProperties 日志采集配置，不能为 {@code null}
     * @param logHandler    日志生命周期处理器，不能为 {@code null}
     */
    public AccessLogAspect(LogProperties logProperties, LogHandler logHandler) {
        this.logProperties = logProperties;
        this.logHandler = logHandler;
    }

    /**
     * 匹配通用 {@code @RequestMapping} 控制器方法。
     */
    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void pointcut() {
    }

    /**
     * 匹配 {@code @GetMapping} 控制器方法。
     */
    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping)")
    public void pointcutGet() {
    }

    /**
     * 匹配 {@code @PostMapping} 控制器方法。
     */
    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void pointcutPost() {
    }

    /**
     * 匹配 {@code @PutMapping} 控制器方法。
     */
    @Pointcut("@annotation(org.springframework.web.bind.annotation.PutMapping)")
    public void pointcutPut() {
    }

    /**
     * 匹配 {@code @DeleteMapping} 控制器方法。
     */
    @Pointcut("@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public void pointcutDelete() {
    }

    /**
     * 匹配 {@code @PatchMapping} 控制器方法。
     */
    @Pointcut("@annotation(org.springframework.web.bind.annotation.PatchMapping)")
    public void pointcutPatch() {
    }

    /**
     * 环绕控制器方法并记录一次访问日志。
     * <p>
     * 非 Web 环境下没有 {@link ServletRequestAttributes}，切面会直接放行业务方法，不制造额外副作用。
     * </p>
     *
     * @param joinPoint 当前 AOP 连接点
     * @return 目标方法的原始返回值
     * @throws Throwable 透传目标方法抛出的异常
     */
    @Around("pointcut() || pointcutGet() || pointcutPost() || pointcutPut() || pointcutDelete() || pointcutPatch()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Instant startTime = Instant.now();
        // 非 Web 环境不记录
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return joinPoint.proceed();
        }
        HttpServletRequest request = attributes.getRequest();
        HttpServletResponse response = attributes.getResponse();
        try {
            // 开始访问日志记录
            logHandler.accessLogStart(
                    AccessLogContext.builder()
                            .startTime(startTime)
                            .request(new RecordableServletHttpRequest(request))
                            .properties(logProperties)
                            .build()
            );
            return joinPoint.proceed();
        } finally {
            Instant endTime = Instant.now();
            logHandler.accessLogFinish(
                    AccessLogContext.builder()
                            .endTime(endTime)
                            .response(new RecordableServletHttpResponse(response))
                            .build()
            );
        }
    }
}
