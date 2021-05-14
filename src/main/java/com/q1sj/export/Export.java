package com.q1sj.export;

import java.util.List;

/**
 * @author Q1sj
 * @date 2020.12.16 16:33
 */
public interface Export {

    /**
     * 对应 ExportRecord 的 code
     * @return
     */
    String getCode();

    /**
     * 获取导出表格对应的实体类
     * @return
     */
    Class<?> getExportEntityClass();
    /**
     * 获取需要导出的list
     * @param conditions
     * @return
     */
    List<?> getList(String conditions);
}
