package com.vanta.starter.encrypt.field.interceptor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import com.vanta.starter.encrypt.field.annotation.FieldEncrypt;
import com.vanta.starter.encrypt.field.util.EncryptHelper;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.type.SimpleTypeRegistry;

import java.lang.reflect.Field;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * 字段解密拦截器
 */
@Intercepts({@Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class})})
public class MyBatisDecryptInterceptor extends AbstractMyBatisInterceptor implements Interceptor {

    /**
     * 执行 intercept 逻辑。
     * 该方法属于 加密能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @param invocation invocation 参数，调用方应传入与 加密能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     * @throws Throwable 当底层客户端、配置解析或远程调用失败时抛出
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 获取执行结果
        Object obj = invocation.proceed();
        if (ObjectUtil.isNull(obj)) {
            return null;
        }
        // 确保目标是 ResultSetHandler
        if (!(invocation.getTarget() instanceof ResultSetHandler)) {
            return obj;
        }
        // 处理查询结果
        if (obj instanceof List<?> resultList) {
            // 处理列表结果
            this.decryptList(resultList);
        } else if (obj instanceof Map<?, ?> map) {
            // 处理Map结果
            this.decryptMap(map);
        } else {
            // 处理单个对象结果
            this.decryptObject(obj);
        }
        return obj;
    }

    /**
     * 解密列表结果
     *
     * @param resultList 结果列表
     */
    private void decryptList(List<?> resultList) {
        if (CollUtil.isEmpty(resultList)) {
            return;
        }
        for (Object result : resultList) {
            decryptObject(result);
        }
    }

    /**
     * 解密Map结果
     *
     * @param resultMap 结果Map
     */
    private void decryptMap(Map<?, ?> resultMap) {
        if (CollUtil.isEmpty(resultMap)) {
            return;
        }
        new HashSet<>(resultMap.values()).forEach(this::decryptObject);
    }

    /**
     * 解密单个对象结果
     *
     * @param result 结果对象
     */
    private void decryptObject(Object result) {
        if (ObjectUtil.isNull(result)) {
            return;
        }
        // String、Integer、Long 等简单类型对象无需处理
        if (SimpleTypeRegistry.isSimpleType(result.getClass())) {
            return;
        }
        // 获取所有字符串类型、需要解密的、有值字段
        List<Field> fieldList = super.getEncryptFields(result);
        if (fieldList.isEmpty()) {
            return;
        }
        // 解密处理
        for (Field field : fieldList) {
            Object fieldValue = ReflectUtil.getFieldValue(result, field);
            if (fieldValue == null) {
                continue;
            }
            String strValue = String.valueOf(fieldValue);
            if (CharSequenceUtil.isBlank(strValue)) {
                continue;
            }
            ReflectUtil.setFieldValue(result, field, EncryptHelper.decrypt(strValue, field
                    .getAnnotation(FieldEncrypt.class)));
        }
    }
}
