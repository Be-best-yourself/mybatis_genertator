package com.common.entity;

public class Page {
	// 页码
	private int pageNo;
	// 第页个数
	private int pageSize;

	public Page(int pageNo,int pageSize) {
		this.pageNo = pageNo;
		this.pageSize = pageSize;
	}
	
	public int getStartSize() {
		if (pageNo<=1) {
			return 0;
		}
		return (pageNo-1)* pageSize;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}
}
