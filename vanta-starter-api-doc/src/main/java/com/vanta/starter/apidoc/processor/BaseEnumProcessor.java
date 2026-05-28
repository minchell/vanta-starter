package com.vanta.starter.apidoc.processor;

import cn.hutool.core.util.ClassUtil;
import com.vanta.starter.core.enums.BaseEnum;
import org.springframework.stereotype.Component;
import top.nextdoc4j.enums.resolver.EnumMetadataResolver;


/**
 * BaseEnumProcessor 类。
 * <p>该类型属于 接口文档能力，负责封装当前 starter 的配置、模型、模板或扩展点。
 * 设计目标是保持 starter 可独立接入、可配置关闭、可通过自定义 Bean 扩展，并避免默认产生远程副作用。</p>
 */
@Component
public class BaseEnumProcessor implements EnumMetadataResolver {

    /**
     * 执行 supports 逻辑。
     * 该方法属于 接口文档能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @param aClass aClass 参数，调用方应传入与 接口文档能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    @Override
    public boolean supports(Class<?> aClass) {
        return aClass != null && aClass.isEnum() && ClassUtil.isAssignable(BaseEnum.class, aClass);
    }

    /**
     * 读取 Enum Interface Type 配置或状态。
     * 该方法只返回当前对象持有的数据，不应修改内部状态或触发远程调用。
     *
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    @Override
    public Class<?> getEnumInterfaceType() {
        return BaseEnum.class;
    }

}
