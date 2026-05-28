package com.vanta.starter.excel.converter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.CharSequenceUtil;
import cn.idev.excel.converters.Converter;
import cn.idev.excel.enums.CellDataTypeEnum;
import cn.idev.excel.metadata.GlobalConfiguration;
import cn.idev.excel.metadata.data.ReadCellData;
import cn.idev.excel.metadata.data.WriteCellData;
import cn.idev.excel.metadata.property.ExcelContentProperty;
import com.vanta.starter.core.constant.StringConstants;

import java.util.List;

/**
 * Excel List 集合转换器
 *
 * <p>
 * 仅适合 List<基本类型> <=> xxx,xxx 转换
 */
public class ExcelListConverter implements Converter<List> {

    /**
     * 执行 supportJavaTypeKey 逻辑。
     * 该方法属于 Excel 处理能力 的公开或内部操作，应保持职责单一，并避免引入业务服务专属耦合。
     *
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    @Override
    public Class supportJavaTypeKey() {
        return List.class;
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
     * 转换 convert To Java Data 的输入数据。
     * 该方法负责在底层客户端模型和业务可读模型之间做边界转换，避免调用方直接依赖底层细节。
     *
     * @param cellData            cellData 参数，调用方应传入与 Excel 处理能力 场景匹配的有效值
     * @param contentProperty     contentProperty 参数，调用方应传入与 Excel 处理能力 场景匹配的有效值
     * @param globalConfiguration globalConfiguration 参数，调用方应传入与 Excel 处理能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    @Override
    public List convertToJavaData(ReadCellData<?> cellData,
                                  ExcelContentProperty contentProperty,
                                  GlobalConfiguration globalConfiguration) {
        String stringValue = cellData.getStringValue();
        return CharSequenceUtil.split(stringValue, StringConstants.COMMA);
    }

    /**
     * 转换 convert To Excel Data 的输入数据。
     * 该方法负责在底层客户端模型和业务可读模型之间做边界转换，避免调用方直接依赖底层细节。
     *
     * @param value               value 参数，调用方应传入与 Excel 处理能力 场景匹配的有效值
     * @param contentProperty     contentProperty 参数，调用方应传入与 Excel 处理能力 场景匹配的有效值
     * @param globalConfiguration globalConfiguration 参数，调用方应传入与 Excel 处理能力 场景匹配的有效值
     * @return 方法执行后的结果对象、配置值或运行时依赖
     */
    @SuppressWarnings("unchecked")
    @Override
    public WriteCellData<Object> convertToExcelData(List value,
                                                    ExcelContentProperty contentProperty,
                                                    GlobalConfiguration globalConfiguration) {
        WriteCellData<Object> writeCellData = new WriteCellData<>(CollUtil.join(value, StringConstants.COMMA));
        writeCellData.setType(CellDataTypeEnum.STRING);
        return writeCellData;
    }
}
