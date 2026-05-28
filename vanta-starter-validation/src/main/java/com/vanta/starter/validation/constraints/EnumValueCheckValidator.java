package com.vanta.starter.validation.constraints;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.text.CharSequenceUtil;
import com.vanta.starter.core.enums.BaseEnum;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * {@link EnumValueCheck} 枚举值校验器。
 * <p>
 * 校验器支持单值、数组和 {@link Iterable} 集合三种输入形式。
 * 判断顺序是：优先使用注解显式配置的 {@code enumValues}，其次使用枚举类和枚举方法或 {@link BaseEnum#getValue()} 判断。
 * </p>
 */
public class EnumValueCheckValidator implements ConstraintValidator<EnumValueCheck, Object> {

    /**
     * 当前校验器日志记录器。
     */
    private static final Logger log = LoggerFactory.getLogger(EnumValueCheckValidator.class);

    /**
     * 注解配置的枚举类型。
     */
    private Class<? extends Enum> enumClass;

    /**
     * 注解显式配置的合法枚举值列表。
     */
    private String[] enumValues;

    /**
     * 获取枚举值的方法名。
     */
    private String enumMethod;

    /**
     * 初始化枚举校验器配置。
     *
     * @param enumValueCheck 枚举校验注解实例。
     */
    @Override
    public void initialize(EnumValueCheck enumValueCheck) {
        this.enumClass = enumValueCheck.value();
        this.enumValues = enumValueCheck.enumValues();
        this.enumMethod = enumValueCheck.method();
    }

    /**
     * 校验输入值是否属于允许的枚举范围。
     *
     * @param value   待校验值，支持单值、数组或集合。
     * @param context 校验上下文。
     * @return {@code true} 表示校验通过，{@code false} 表示校验失败。
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        // 处理数组场景
        if (value.getClass().isArray()) {
            Object[] array = (Object[]) value;
            for (Object element : array) {
                if (!isValidElement(element)) {
                    return false;
                }
            }
            return true;
        }

        // 处理集合场景
        if (value instanceof Iterable<?> iterable) {
            for (Object element : iterable) {
                if (!isValidElement(element)) {
                    return false;
                }
            }
            return true;
        }

        // 处理单个值场景
        return isValidElement(value);
    }

    /**
     * 校验单个元素是否有效
     *
     * @param value 待校验的值
     * @return 是否有效
     */
    private boolean isValidElement(Object value) {
        // 优先校验 enumValues
        if (enumValues.length > 0) {
            return Arrays.asList(enumValues).contains(Convert.toStr(value));
        }

        Enum[] enumConstants = enumClass.getEnumConstants();
        if (enumConstants.length == 0) {
            return false;
        }

        if (CharSequenceUtil.isBlank(enumMethod)) {
            return findEnumValue(enumConstants, Convert.toStr(value));
        }

        try {
            // 枚举类指定了方法名，则调用指定方法获取枚举值
            Method method = enumClass.getMethod(enumMethod);
            for (Enum enumConstant : enumConstants) {
                if (Convert.toStr(method.invoke(enumConstant)).equals(Convert.toStr(value))) {
                    return true;
                }
            }
        } catch (Exception e) {
            log.error("An error occurred while validating the enum value, please check the @EnumValue parameter configuration.", e);
        }
        return false;
    }

    /**
     * 遍历枚举类，判断是否包含指定值
     *
     * @param enumConstants 枚举类数组
     * @param value         待校验的值
     * @return 是否包含指定值
     */
    private boolean findEnumValue(Enum[] enumConstants, Object value) {
        for (Enum enumConstant : enumConstants) {
            if (enumConstant instanceof BaseEnum<?> baseEnum) {
                if (baseEnum.getValue().toString().equals(value)) {
                    return true;
                }
            } else if (enumConstant.toString().equalsIgnoreCase(Convert.toStr(value))) {
                return true;
            }
        }
        return false;
    }
}
