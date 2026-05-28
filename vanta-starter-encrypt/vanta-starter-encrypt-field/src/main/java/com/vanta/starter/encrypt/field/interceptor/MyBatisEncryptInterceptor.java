package com.vanta.starter.encrypt.field.interceptor;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.core.conditions.AbstractWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.vanta.starter.core.constant.StringConstants;
import com.vanta.starter.encrypt.field.annotation.FieldEncrypt;
import com.vanta.starter.encrypt.field.util.EncryptHelper;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字段加密拦截器
 */
public class MyBatisEncryptInterceptor extends AbstractMyBatisInterceptor implements InnerInterceptor {

    /**
     * PARAM_PAIRS_PATTERN 字段。
     * <p>用于保存 加密能力 的配置值、依赖对象或内部状态。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private static final Pattern PARAM_PAIRS_PATTERN = Pattern
            .compile("#\\{ew\\.paramNameValuePairs\\.(" + Constants.WRAPPER_PARAM + "\\d+)\\}");

    /**
     * 执行 beforeQuery 逻辑。
     * 该方法属于 加密能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @param executor        executor 参数，调用方应传入与 加密能力 场景匹配的有效值
     * @param mappedStatement mappedStatement 参数，调用方应传入与 加密能力 场景匹配的有效值
     * @param parameterObject parameterObject 参数，调用方应传入与 加密能力 场景匹配的有效值
     * @param rowBounds       rowBounds 参数，调用方应传入与 加密能力 场景匹配的有效值
     * @param resultHandler   resultHandler 参数，调用方应传入与 加密能力 场景匹配的有效值
     * @param boundSql        boundSql 参数，调用方应传入与 加密能力 场景匹配的有效值
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void beforeQuery(Executor executor,
                            MappedStatement mappedStatement,
                            Object parameterObject,
                            RowBounds rowBounds,
                            ResultHandler resultHandler,
                            BoundSql boundSql) {
        if (parameterObject == null) {
            return;
        }
        if (parameterObject instanceof Map parameterMap) {
            this.encryptQueryParameter(parameterMap, mappedStatement);
        }
    }

    /**
     * 执行 beforeUpdate 逻辑。
     * 该方法属于 加密能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @param executor        executor 参数，调用方应传入与 加密能力 场景匹配的有效值
     * @param mappedStatement mappedStatement 参数，调用方应传入与 加密能力 场景匹配的有效值
     * @param parameterObject parameterObject 参数，调用方应传入与 加密能力 场景匹配的有效值
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void beforeUpdate(Executor executor, MappedStatement mappedStatement, Object parameterObject) {
        if (parameterObject == null) {
            return;
        }
        if (parameterObject instanceof Map parameterMap) {
            // 带别名方法（使用 @Param 注解的场景）
            this.encryptMap(parameterMap, mappedStatement);
        } else {
            // 无别名方法（例如：MP insert 等方法）
            this.encryptEntity(super.getEncryptFields(parameterObject), parameterObject);
        }
    }

    /**
     * 加密查询参数（针对 Map 类型参数）
     *
     * @param parameterMap    参数
     * @param mappedStatement 映射语句
     */
    private void encryptQueryParameter(Map<String, Object> parameterMap, MappedStatement mappedStatement) {
        Map<String, FieldEncrypt> encryptParameterMap = super.getEncryptParameters(mappedStatement);
        for (Map.Entry<String, Object> parameterEntrySet : parameterMap.entrySet()) {
            String parameterName = parameterEntrySet.getKey();
            Object parameterValue = parameterEntrySet.getValue();
            if (parameterValue == null || ClassUtil.isBasicType(parameterValue
                    .getClass()) || parameterValue instanceof AbstractWrapper) {
                continue;
            }
            if (parameterValue instanceof String str) {
                FieldEncrypt fieldEncrypt = encryptParameterMap.get(parameterName);
                if (fieldEncrypt != null) {
                    parameterMap.put(parameterName, this.doEncrypt(str, fieldEncrypt));
                }
            } else {
                // 实体参数
                this.encryptEntity(super.getEncryptFields(parameterValue), parameterValue);
            }
        }
    }

    /**
     * 处理实体加密
     *
     * @param fieldList 加密字段列表
     * @param entity    实体
     */
    private void encryptEntity(List<Field> fieldList, Object entity) {
        for (Field field : fieldList) {
            Object fieldValue = ReflectUtil.getFieldValue(entity, field);
            if (fieldValue == null) {
                continue;
            }
            String strValue = String.valueOf(fieldValue);
            if (CharSequenceUtil.isBlank(strValue)) {
                continue;
            }
            ReflectUtil.setFieldValue(entity, field, EncryptHelper.encrypt(strValue, field
                    .getAnnotation(FieldEncrypt.class)));
        }
    }

    /**
     * 加密 Map 类型数据（使用 @Param 注解的场景）
     *
     * @param parameterMap    参数
     * @param mappedStatement 映射语句
     */
    private void encryptMap(Map<String, Object> parameterMap, MappedStatement mappedStatement) {
        Object parameter;
        // 别名带有 et（针对 MP 的 updateById、update 等方法）
        if (parameterMap.containsKey(Constants.ENTITY) && (parameter = parameterMap.get(Constants.ENTITY)) != null) {
            this.encryptEntity(super.getEncryptFields(parameter), parameter);
        }
        // 别名带有 ew（针对 MP 的 UpdateWrapper、LambdaUpdateWrapper 等参数）
        if (parameterMap.containsKey(Constants.WRAPPER) && (parameter = parameterMap.get(Constants.WRAPPER)) != null) {
            this.encryptUpdateWrapper(parameter, mappedStatement);
        }
    }

    /**
     * 处理 UpdateWrapper 类型参数加密（针对 MP 的 UpdateWrapper、LambdaUpdateWrapper 等参数）
     *
     * @param parameter       Wrapper 参数
     * @param mappedStatement 映射语句
     * @author cary
     * @author wangshaopeng@talkweb.com.cn（<a
     * href="https://blog.csdn.net/tianmaxingkonger/article/details/130986784">基于Mybatis-Plus拦截器实现MySQL数据加解密</a>）
     * @since 2.1.1
     */
    @SuppressWarnings("unchecked")
    private void encryptUpdateWrapper(Object parameter, MappedStatement mappedStatement) {
        if (parameter instanceof AbstractWrapper updateWrapper) {
            String sqlSet = updateWrapper.getSqlSet();
            if (CharSequenceUtil.isBlank(sqlSet)) {
                return;
            }
            // 将 name=#{ew.paramNameValuePairs.xxx},age=#{ew.paramNameValuePairs.xxx} 切出来
            String[] elArr = sqlSet.split(StringConstants.COMMA);
            Map<String, String> propMap = new HashMap<>(elArr.length);
            Arrays.stream(elArr).forEach(el -> {
                String[] elPart = el.split(StringConstants.EQUALS);
                propMap.put(elPart[0], elPart[1]);
            });
            // 获取加密字段
            Class<?> entityClass = this.getEntityClass(updateWrapper, mappedStatement);
            List<Field> encryptFieldList = super.getEncryptFields(entityClass);
            for (Field field : encryptFieldList) {
                FieldEncrypt fieldEncrypt = field.getAnnotation(FieldEncrypt.class);
                String el = propMap.get(field.getName());
                if (CharSequenceUtil.isBlank(el)) {
                    continue;
                }
                Matcher matcher = PARAM_PAIRS_PATTERN.matcher(el);
                if (matcher.matches()) {
                    String valueKey = matcher.group(1);
                    updateWrapper.getParamNameValuePairs()
                            .compute(valueKey, (k, value) -> this.doEncrypt(value, fieldEncrypt));
                }
            }
        }
    }

    /**
     * 处理加密
     *
     * @param parameterValue 参数值
     * @param fieldEncrypt   字段加密注解
     */
    private Object doEncrypt(Object parameterValue, FieldEncrypt fieldEncrypt) {
        if (ObjectUtil.isNull(parameterValue)) {
            return null;
        }
        String strValue = String.valueOf(parameterValue);
        if (CharSequenceUtil.isBlank(strValue)) {
            return null;
        }
        return EncryptHelper.encrypt(strValue, fieldEncrypt);
    }

    /**
     * 获取实体类
     *
     * @param wrapper         查询或更新包装器
     * @param mappedStatement 映射语句
     * @return 实体类
     */
    private Class<?> getEntityClass(AbstractWrapper wrapper, MappedStatement mappedStatement) {
        // 尝试从 Wrapper 中获取实体类
        Class<?> entityClass = wrapper.getEntityClass();
        if (entityClass != null) {
            return entityClass;
        }
        // 从映射语句中获取实体类
        return mappedStatement.getParameterMap().getType();
    }
}
