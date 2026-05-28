package com.vanta.starter.trace.handler;

import com.yomahub.tlog.constant.TLogConstants;
import com.yomahub.tlog.core.rpc.TLogLabelBean;
import com.yomahub.tlog.core.rpc.TLogRPCHandler;
import jakarta.servlet.http.HttpServletRequest;


/**
 * TLogWebCommon 类。
 * <p>该类型属于 日志能力，负责封装当前 starter 的配置、模型、模板或扩展点。
 * 设计目标是保持 starter 可独立接入、可配置关闭、可通过自定义 Bean 扩展，并避免默认产生远程副作用。</p>
 */
public class TLogWebCommon extends TLogRPCHandler {

    /**
     * tLogWebCommon 字段。
     * <p>用于保存 日志能力 的日志组件，用于记录 starter 内部关键状态和异常信息。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private static volatile TLogWebCommon tLogWebCommon;

    /**
     * 执行 loadInstance 逻辑。
     * 该方法属于 日志能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    public static TLogWebCommon loadInstance() {
        if (tLogWebCommon == null) {
            synchronized (TLogWebCommon.class) {
                if (tLogWebCommon == null) {
                    tLogWebCommon = new TLogWebCommon();
                }
            }
        }
        return tLogWebCommon;
    }

    /**
     * 执行 preHandle 逻辑。
     * 该方法属于 日志能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @param request request 参数，调用方应传入与 日志能力 场景匹配的有效值
     */
    public void preHandle(HttpServletRequest request) {
        String traceId = request.getHeader(TLogConstants.TLOG_TRACE_KEY);
        String spanId = request.getHeader(TLogConstants.TLOG_SPANID_KEY);
        String preIvkApp = request.getHeader(TLogConstants.PRE_IVK_APP_KEY);
        String preIvkHost = request.getHeader(TLogConstants.PRE_IVK_APP_HOST);
        String preIp = request.getHeader(TLogConstants.PRE_IP_KEY);
        TLogLabelBean labelBean = new TLogLabelBean(preIvkApp, preIvkHost, preIp, traceId, spanId);
        processProviderSide(labelBean);
    }

    /**
     * 执行 afterCompletion 逻辑。
     * 该方法属于 日志能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     */
    public void afterCompletion() {
        cleanThreadLocal();
    }
}
