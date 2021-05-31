package com.q1sj.export;

import com.alibaba.excel.EasyExcel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Q1sj
 * @date 2020.12.16 16:33
 */
public class ExportContext<T extends AbstractExportRecord> {
    private final Map<String, Export> exportMap = new HashMap<>(16);
    private final int excelMaxRows;
    private final String excelSavePath;
    private final Logger log = LoggerFactory.getLogger(ExportContext.class);
    private final boolean enableSubList;
    private ExportStatusChangeListener<T> statusChangeListener = new DefaultStatusChangeListener();

    public ExportContext(Collection<Export> exportList,
                         int excelMaxRows,
                         String excelSavePath,
                         boolean enableSubList) {
        this(exportList, excelMaxRows, excelSavePath, null, enableSubList);
    }

    public ExportContext(Collection<Export> exportList,
                         int excelMaxRows,
                         String excelSavePath,
                         ExportStatusChangeListener<T> statusChangeListener,
                         boolean enableSubList) {
        this.excelMaxRows = excelMaxRows;
        this.excelSavePath = excelSavePath;
        if (statusChangeListener != null) {
            this.statusChangeListener = statusChangeListener;
        }
        this.enableSubList = enableSubList;
        for (Export export : exportList) {
            exportMap.put(export.getCode(), export);
        }
    }

    public void export(Collection<T> exportRecords) {
        Objects.requireNonNull(exportRecords);
        exportRecords.forEach(export -> {
            export.setStatus(EnumExportStatus.ING.value);
            export.setStatusName(EnumExportStatus.ING.desc);
            statusChangeListener.accept(export);
        });
        exportRecords.forEach(this::export);
    }

    public void export(T exportRecord) {
        Objects.requireNonNull(exportRecord);
        if (EnumExportStatus.ING.value != exportRecord.getStatus()) {
            exportRecord.setStatus(EnumExportStatus.ING.value);
            exportRecord.setStatusName(EnumExportStatus.ING.desc);
            statusChangeListener.accept(exportRecord);
        }
        try {
            List<?> list = null;
            // 获取要导出数据
            String conditions = exportRecord.getConditions();
            String code = exportRecord.getCode();
            Export export = getExport(code);
            list = export.getList(conditions);
            if (list == null || list.isEmpty()) {
                throw new ExportException(exportRecord, "export list is empty");
            }
            // 导出
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            // 生成导出目录
            String filePath = excelSavePath + sdf.format(new Date()) + "/" + exportRecord.getFileName();
            log.info("start export {}", filePath);
            // 目录不存在 自动创建
            new File(filePath).mkdirs();
            // 获取导出实体类
            this.exportFile(list, filePath, export.getExportEntityClass());
            packageFile(exportRecord, filePath);
            log.info("end export {}", filePath);
        } catch (Exception e) {
            e.printStackTrace();
            exportRecord.setStatus(EnumExportStatus.FAIL.value);
            exportRecord.setStatusName(EnumExportStatus.FAIL.desc);
            exportRecord.setFailReason(e.getMessage());
        } finally {
            statusChangeListener.accept(exportRecord);
        }
    }

    /**
     * 压缩
     *
     * @param exportRecord
     * @param filePath
     */
    private void packageFile(T exportRecord, String filePath) throws IOException {

        if (FileUtils.zipFiles(filePath, "*", filePath + ".zip")) {
            // 打包完成 更新数据库导出状态
            exportRecord.setStatus(EnumExportStatus.SUCCESS.value);
            exportRecord.setStatusName(EnumExportStatus.SUCCESS.desc);
            exportRecord.setFilePath(filePath + ".zip");
            org.apache.commons.io.FileUtils.deleteDirectory(new File(filePath));
        } else {
            throw new ExportException(exportRecord, filePath + "package fail");
        }
    }

    /**
     * 导出文件
     *
     * @param list
     * @param filePath
     * @param exportEntityClass
     */
    private void exportFile(List<?> list, String filePath, Class<?> exportEntityClass) {
        if (enableSubList) {
            // 大list分割 多excel导出
            // 获取excel_max_rows
            // 分割
            List<? extends List<?>> lists = ListUtils.subList(list, excelMaxRows);
            for (int i = 0; i < lists.size(); i++) {
                EasyExcel.write(filePath + "/" + i + ".xlsx", exportEntityClass)
                        .sheet("sheet1")
                        .doWrite(lists.get(i));
            }
        } else {
            EasyExcel.write(filePath + "/0.xlsx", exportEntityClass)
                    .sheet("sheet1")
                    .doWrite(list);
        }
    }


    private Export getExport(String code) {
        Export export = exportMap.get(code);
        if (export == null) {
            throw new ExportException(null, "当前code：" + code + "不能导出");
        }
        return export;
    }

    private class DefaultStatusChangeListener implements ExportStatusChangeListener<T> {

        @Override
        public void accept(T record) {
            log.info("update status {}", record.getStatusName());
        }
    }

}
