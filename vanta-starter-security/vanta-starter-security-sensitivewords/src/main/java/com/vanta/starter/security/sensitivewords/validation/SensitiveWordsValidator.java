package com.vanta.starter.security.sensitivewords.validation;

import com.vanta.starter.security.sensitivewords.service.SensitiveWordsService;
import jakarta.annotation.Resource;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

/**
 * 敏感词校验器
 */
public class SensitiveWordsValidator implements ConstraintValidator<SensitiveWords, String> {

    /**
     * sensitiveWordsService 字段。
     * <p>用于保存 安全防护能力 的底层客户端或模板依赖，业务方可以通过自定义 Bean 替换。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    @Resource
    private SensitiveWordsService sensitiveWordsService;

    /**
     * 读取 Valid 配置或状态。
     * 该方法只返回当前对象持有的数据，不应修改内部状态或触发远程调用。
     *
     * @param value   value 参数，调用方应传入与 安全防护能力 场景匹配的有效值
     * @param context context 参数，调用方应传入与 安全防护能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        List<String> res = sensitiveWordsService.check(value);
        if (!res.isEmpty()) {
            // 禁用默认消息
            context.disableDefaultConstraintViolation();
            // 动态设置错误消息
            context.buildConstraintViolationWithTemplate("内容包含敏感词汇: " + String.join(",", res)).addConstraintViolation();
            return false;
        }
        return true;
    }
}
