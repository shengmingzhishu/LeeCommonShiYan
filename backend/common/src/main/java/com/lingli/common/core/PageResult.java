package com.lingli.common.core;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.util.List;

/**
 * 分页结果
 *
 * @author lingli
 * @since 2023-11-28
 */
@Data
public class PageResult<T> {
    
    /**
     * 记录列表
     */
    private List<T> records;
    
    /**
     * 总记录数
     */
    private Long total;
    
    /**
     * 每页显示数量
     */
    private Long size;
    
    /**
     * 当前页码
     */
    private Long current;
    
    /**
     * 总页数
     */
    private Long pages;

    public PageResult() {
    }

    public PageResult(List<T> records, Long total, Long size, Long current, Long pages) {
        this.records = records;
        this.total = total;
        this.size = size;
        this.current = current;
        this.pages = pages;
    }

    /**
     * 从MyBatis-Plus Page对象转换
     */
    public static <T> PageResult<T> of(Page<T> page) {
        return new PageResult<>(
            page.getRecords(),
            page.getTotal(),
            page.getSize(),
            page.getCurrent(),
            page.getPages()
        );
    }

    /**
     * 手动构建分页结果
     */
    public static <T> PageResult<T> of(List<T> records, Long total, Long current, Long size) {
        long pages = (total + size - 1) / size;
        return new PageResult<>(records, total, size, current, pages);
    }
}