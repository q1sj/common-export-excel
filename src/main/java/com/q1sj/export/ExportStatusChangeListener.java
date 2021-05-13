package com.q1sj.export;

/**
 * @author Q1sj
 * @date 2021.5.10 8:49
 */
public interface ExportStatusChangeListener<T extends AbstractExportRecord> {
    /**
     * ExportRecord 状态被修改是会调用此方法
     * @param record
     */
    void accept(T record);
}
