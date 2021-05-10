package com.qsj.export;

import java.util.List;

/**
 * @author Q1sj
 * @date 2020.12.16 16:33
 */
public interface Export {
    /**
     * 获取导出事件类型
     * @return
     */
    String getExportCode();

    /**
     * 获取需要导出的list
     * @param conditions
     * @return
     */
    List<?> getExportList(String conditions);
}
