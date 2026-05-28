package com.vanta.starter.excel.converter;

import cn.idev.excel.converters.Converter;
import cn.idev.excel.enums.CellDataTypeEnum;
import cn.idev.excel.metadata.GlobalConfiguration;
import cn.idev.excel.metadata.data.ReadCellData;
import cn.idev.excel.metadata.data.WriteCellData;
import cn.idev.excel.metadata.property.ExcelContentProperty;
import com.vanta.starter.core.constant.StringConstants;
import com.vanta.starter.core.enums.BaseEnum;

/**
 * Excel 枚举接口转换器
 *
 * @see BaseEnum
 */
public class ExcelBaseEnumConverter implements Converter<BaseEnum<?>> {

    /**
     * 执行 supportJavaTypeKey 逻辑。
     * 该方法属于 Excel 处理能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    @Override
    public Class<BaseEnum> supportJavaTypeKey() {
        return BaseEnum.class;
    }

    /**
     * 执行 supportExcelTypeKey 逻辑。
     * 该方法属于 Excel 处理能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    /**
     * 转换为 Java 数据（读取 Excel）
     */
    @Override
    public BaseEnum<?> convertToJavaData(ReadCellData<?> cellData,
                                         ExcelContentProperty contentProperty,
                                         GlobalConfiguration globalConfiguration) {
        return BaseEnum.getByDescription(cellData.getStringValue(), contentProperty.getField().getType());
    }

    /**
     * 转换为 Excel 数据（写入 Excel）
     */
    @Override
    public WriteCellData<String> convertToExcelData(BaseEnum<?> value,
                                                    ExcelContentProperty contentProperty,
                                                    GlobalConfiguration globalConfiguration) {
        if (value == null) {
            return new WriteCellData<>(StringConstants.EMPTY);
        }
        return new WriteCellData<>(value.getDescription());
    }
}
