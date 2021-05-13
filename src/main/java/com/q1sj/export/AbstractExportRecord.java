package com.q1sj.export;

import java.io.Serializable;

/**
 * 导出记录表
 *
 * @author qsj
 * @date 2021-04-01 14:43:54
 */

public abstract class AbstractExportRecord implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 文件名称 不带后缀
	 */
	private String fileName;
	/**
	 * 导出类型
	 * 根据code找到对应export实现类
	 */
	private String code;
	/**
	 * 下载条件
	 */
	private String conditions;
	/**
	 * 导出状态：1等待导出、2导出中、3导出成功、4导出失败
	 */
	private int status;
	private String statusName;
	/**
	 * 导出失败原因
	 */
	private String failReason;
	/**
	 * 文件绝对路径
	 */
	private String filePath;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getConditions() {
		return conditions;
	}

	public void setConditions(String conditions) {
		this.conditions = conditions;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public String getFailReason() {
		return failReason;
	}

	public void setFailReason(String failReason) {
		this.failReason = failReason;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	@Override
	public String toString() {
		return "AbstractExportRecord{" +
				"fileName='" + fileName + '\'' +
				", code='" + code + '\'' +
				", conditions='" + conditions + '\'' +
				", status=" + status +
				", statusName='" + statusName + '\'' +
				", failReason='" + failReason + '\'' +
				", filePath='" + filePath + '\'' +
				'}';
	}
}
