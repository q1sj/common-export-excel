package com.qsj.export;

import com.alibaba.excel.annotation.ExcelProperty;

import java.util.Arrays;
import java.util.List;

/**
 * @author Q1sj
 * @date 2021.5.7 17:10
 */
public class ExportTest {
    public static void main(String[] args) {
        List<Export> exportList = Arrays.asList(new UserExport());
        ExportStatusChangeListener<ExportRecord> changeListener = System.out::println;
        ExportContext<ExportRecord> exportContext = new ExportContext<>(exportList, 1000, "C:/", changeListener);
        // 模拟从数据库查询待导出记录
        List<ExportRecord> exports = Arrays.asList(ExportRecord.mock());
        exportContext.export(exports);

    }
}

class ExportRecord extends AbstractExportRecord{
    public static ExportRecord mock(){
        ExportRecord exportRecord = new ExportRecord();
        exportRecord.setFileName("filename");
        exportRecord.setConditions("xxx");
        exportRecord.setCode("user");
        return exportRecord;
    }
}

class UserExport implements Export{

    @Override
    public String getExportCode() {
        return "user";
    }

    @Override
    public List<?> getExportList(String conditions) {
        // 此处模拟 根据条件查询 返回结果集
        return Arrays.asList(new User("qsj",18),new User("admin",19));
    }
}

class User{
    @ExcelProperty("用户名")
    private String username;
    @ExcelProperty("年龄")
    private int age;

    public User(String username, int age) {
        this.username = username;
        this.age = age;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
