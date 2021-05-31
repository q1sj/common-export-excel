package com.q1sj.export;

/**
 * @author Q1sj
 * @date 2021.4.30 16:17
 */
public class ExportException extends RuntimeException {

    private final AbstractExportRecord exportRecord;

    public ExportException(AbstractExportRecord exportRecord,String message) {
        super(message);
        this.exportRecord = exportRecord;
    }

    public AbstractExportRecord getExportRecord() {
        return exportRecord;
    }
}
