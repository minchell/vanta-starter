package com.vanta.starter.excel.util;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.URLUtil;
import cn.idev.excel.FastExcelFactory;
import cn.idev.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.vanta.starter.core.exception.BaseException;
import com.vanta.starter.excel.converter.ExcelBigNumberConverter;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Excel 工具类
 */
public class ExcelUtils {

    /**
     * log 字段。
     * <p>用于保存 Excel 处理能力 的日志组件，用于记录 starter 内部关键状态和异常信息。
     * 该字段只服务于 starter 的配置绑定、默认装配或内部执行，不应承载 具体业务项目专属状态。</p>
     */
    private static final Logger log = LoggerFactory.getLogger(ExcelUtils.class);

    private ExcelUtils() {
    }

    /**
     * 导出
     *
     * @param list     导出数据集合
     * @param fileName 文件名
     * @param clazz    导出数据类型
     * @param response 响应对象
     */
    public static <T> void export(List<T> list, String fileName, Class<T> clazz, HttpServletResponse response) {
        export(list, fileName, "Sheet1", Collections.emptySet(), clazz, response);
    }

    /**
     * 导出
     *
     * @param list                    导出数据集合
     * @param fileName                文件名
     * @param sheetName               工作表名称
     * @param excludeColumnFieldNames 排除字段
     * @param clazz                   导出数据类型
     * @param response                响应对象
     */
    public static <T> void export(List<T> list,
                                  String fileName,
                                  String sheetName,
                                  Set<String> excludeColumnFieldNames,
                                  Class<T> clazz,
                                  HttpServletResponse response) {
        try {
            String exportFileName = URLUtil.encode("%s_%s.xlsx".formatted(fileName, DateUtil
                    .format(new Date(), DatePattern.PURE_DATETIME_PATTERN)));
            response.setHeader("Content-disposition", "attachment;filename=" + exportFileName);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
            FastExcelFactory.write(response.getOutputStream(), clazz)
                    .autoCloseStream(false)
                    // 自动适配宽度
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    // 自动转换大数值
                    .registerConverter(new ExcelBigNumberConverter())
                    .sheet(sheetName)
                    .excludeColumnFieldNames(excludeColumnFieldNames)
                    .doWrite(list);
        } catch (Exception e) {
            log.error("Export excel occurred an error: {}. fileName: {}.", e.getMessage(), fileName, e);
            response.reset();
            throw new BaseException("导出 Excel 出现错误");
        }
    }
}
