package com.vanta.starter.excel.model;

import java.util.LinkedHashMap;

/**
 * Excel 字段信息
 */
public class ExcelClassField {

    /**
     * 字段名称
     */
    private String fieldName;

    /**
     * 表头名称
     */
    private String name;

    /**
     * 映射关系
     */
    private LinkedHashMap<String, String> kvMap;

    /**
     * 示例值
     */
    private Object example;

    /**
     * 排序
     */
    private int sort;

    /**
     * 是否为注解字段：0-否，1-是
     */
    private int hasAnnotation;

    /**
     * 获取字段名称。
     *
     * @return 字段名称
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * 设置字段名称。
     *
     * @param fieldName 字段名称
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * 获取表头名称。
     *
     * @return 表头名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置表头名称。
     *
     * @param name 表头名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取映射关系。
     *
     * @return 映射关系
     */
    public LinkedHashMap<String, String> getKvMap() {
        return kvMap;
    }

    /**
     * 设置映射关系。
     *
     * @param kvMap 映射关系
     */
    public void setKvMap(LinkedHashMap<String, String> kvMap) {
        this.kvMap = kvMap;
    }

    /**
     * 获取示例值。
     *
     * @return 示例值
     */
    public Object getExample() {
        return example;
    }

    /**
     * 设置示例值。
     *
     * @param example 示例值
     */
    public void setExample(Object example) {
        this.example = example;
    }

    /**
     * 获取排序。
     *
     * @return 排序
     */
    public int getSort() {
        return sort;
    }

    /**
     * 设置排序。
     *
     * @param sort 排序
     */
    public void setSort(int sort) {
        this.sort = sort;
    }

    /**
     * 获取是否为注解字段：0-否，1-是。
     *
     * @return 是否为注解字段：0-否，1-是
     */
    public int getHasAnnotation() {
        return hasAnnotation;
    }

    /**
     * 设置是否为注解字段：0-否，1-是。
     *
     * @param hasAnnotation 是否为注解字段：0-否，1-是
     */
    public void setHasAnnotation(int hasAnnotation) {
        this.hasAnnotation = hasAnnotation;
    }
}
