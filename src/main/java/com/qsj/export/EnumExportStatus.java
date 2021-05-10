package com.qsj.export;

/**
 * 导出状态枚举
 * @author Q1sj
 * @date 2021.4.9 17:05
 */
public enum EnumExportStatus {
    //0待导出、 1导出中、2导出成功、3导出失败
    UNKNOWN(-1,"未知"),
    WAIT(0,"待导出"),
    ING(1,"导出中"),
    SUCCESS(2,"导出成功"),
    FAIL(3,"导出失败"),
    ;

    public final int value;
    public final String desc;

    EnumExportStatus(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static EnumExportStatus getByValue(int value){
        for (EnumExportStatus status : values()) {
            if (status.value==value) {
                return status;
            }
        }
        return UNKNOWN;
    }
}
