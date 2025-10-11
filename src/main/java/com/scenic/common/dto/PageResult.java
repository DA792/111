package com.scenic.common.dto;

import java.util.List;

/**
 * 分页结果类
 */
public class PageResult<T> {
    private long total;
    private int pageSize;
    private int currentPage;
    private List<T> records;

    // 构造函数
    public PageResult() {}

    public PageResult(long total, int pageSize, int currentPage, List<T> records) {
        this.total = total;
        this.pageSize = pageSize;
        this.currentPage = currentPage;
        this.records = records;
    }

    // 静态方法创建分页结果
    public static <T> PageResult<T> of(long total, int pageSize, int currentPage, List<T> records) {
        return new PageResult<>(total, pageSize, currentPage, records);
    }

    // Getter 和 Setter 方法
    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    @Override
    public String toString() {
        return "PageResult{" +
                "total=" + total +
                ", pageSize=" + pageSize +
                ", currentPage=" + currentPage +
                ", records=" + records +
                '}';
    }
}
