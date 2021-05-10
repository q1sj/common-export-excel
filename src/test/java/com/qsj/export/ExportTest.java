package com.qsj.export;

import java.util.Arrays;
import java.util.List;

/**
 * @author Q1sj
 * @date 2021.5.7 17:10
 */
public class ExportTest {
    public static void main(String[] args) {
        ExportContext<ExportRecord> exportContext = new ExportContext<>(Arrays.asList(new ExportImpl()), 1000, "C:/",System.out::println);
        // 模拟查询待导出记录
        List<ExportRecord> exports = Arrays.asList(new ExportRecord());
        exportContext.export(exports);
    }
}

class ExportRecord extends AbstractExportRecord{
    public ExportRecord() {
        setCode("1");
        setConditions("xxx");
        setFileName("filename");
    }

}

class ExportImpl implements Export{

    @Override
    public String getExportCode() {
        return "1";
    }

    @Override
    public List<?> getExportList(String conditions) {
        return Arrays.asList(new ExportRecord());
    }
}
