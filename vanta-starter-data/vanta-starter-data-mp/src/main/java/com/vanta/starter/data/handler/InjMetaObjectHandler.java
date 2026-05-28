package com.vanta.starter.data.handler;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.vanta.starter.core.util.DateUtils;
import com.vanta.starter.core.util.Tuple3;
import org.apache.ibatis.reflection.MetaObject;

/**
 * MyBatis-Plus 默认元对象自动填充处理器。
 * <p>
 * 该处理器默认填充 {@code deleted}、{@code createdAt}、{@code updatedAt} 字段，
 * 并允许业务方通过 {@link MetaObjectFiledHandler} 扩展新增和更新时的填充字段。
 * </p>
 */
public class InjMetaObjectHandler implements MetaObjectHandler {

    /**
     * 业务自定义字段填充处理器。
     */
    private final MetaObjectFiledHandler handler;

    /**
     * 创建默认元对象自动填充处理器。
     * <p>
     * 构造时会从 Spring 容器查找第一个 {@link MetaObjectFiledHandler} Bean，存在则用于扩展填充。
     * </p>
     */
    public InjMetaObjectHandler() {
        var beanMap = SpringUtil.getBeansOfType(MetaObjectFiledHandler.class);

        if (beanMap != null) {
            this.handler = beanMap.values().stream().findFirst().orElse(null);
        } else {
            this.handler = null;
        }
    }

    /**
     * 执行新增时字段自动填充。
     *
     * @param metaObject MyBatis-Plus 元对象。
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        if (metaObject == null) {
            return;
        }

        this.fillFieldValue(metaObject, "deleted", 0, false);
        this.fillFieldValue(metaObject, "createdAt", DateUtils.cnNow(), true);
        this.fillFieldValue(metaObject, "updatedAt", DateUtils.cnNow(), true);

        if (this.handler != null) {
            Tuple3<String, Object, Boolean>[] tuples = handler.insertFillFieldMap();

            if (tuples != null) {
                for (Tuple3<String, Object, Boolean> tuple : tuples) {
                    this.fillFieldValue(metaObject, tuple.getFirst(), tuple.getSecond(), tuple.getThree() != null && tuple.getThree());
                }
            }
        }

    }

    /**
     * 执行更新时字段自动填充。
     *
     * @param metaObject MyBatis-Plus 元对象。
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        if (metaObject == null) {
            return;
        }

        this.fillFieldValue(metaObject, "updatedAt", DateUtils.cnNow(), true);

        if (this.handler != null) {
            Tuple3<String, Object, Boolean>[] tuples = handler.updateFillFieldMap();

            if (tuples != null) {
                for (Tuple3<String, Object, Boolean> tuple : tuples) {
                    this.fillFieldValue(metaObject, tuple.getFirst(), tuple.getSecond(), tuple.getThree() != null && tuple.getThree());
                }
            }
        }
    }

    /**
     * 填充字段值
     *
     * @param metaObject     元数据对象
     * @param fieldName      要填充的字段名
     * @param fillFieldValue 要填充的字段值
     * @param isOverride     如果字段值不为空，是否覆盖（true：覆盖；false：不覆盖）
     */
    private void fillFieldValue(MetaObject metaObject, String fieldName, Object fillFieldValue, boolean isOverride) {
        if (metaObject.hasSetter(fieldName) && metaObject.hasGetter(fieldName)) {
            Object fieldValue = metaObject.getValue(fieldName);
            setFieldValByName(fieldName, fieldValue != null && !isOverride ? fieldValue : fillFieldValue, metaObject);
        }
    }

}
