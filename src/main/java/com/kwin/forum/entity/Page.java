package com.kwin.forum.entity;

/**
 * 封装分页的一些信息
 */
public class Page {
    //当前页码
    private int current = 1;
    //显示上限
    private int limit = 10;
    //数据总数(用于计算总页数)
    private int rows;
    //查询路径(用于复用分页链接)
    private String path;

    public int getCurrent() {
        return current;
    }
    public int getLimit() {
        return limit;
    }
    public int getRows() {
        return rows;
    }
    public String getPath() {
        return path;
    }

    public void setLimit(int limit) {
        if (limit >= 1 && limit <= 100) {
            this.limit = limit;
        }
    }
    public void setCurrent(int current) {
        this.current = current;
    }
    public void setRows(int rows) {
        if (rows >= 0) {
            this.rows = rows;
        }
    }
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 通过当前页的页码获取当前页的起始行
     * @return
     */
    public int getOffSet() {
        return (current - 1) * limit;
    }

    /**
     * 获取总页数
     * @return
     */
    public int getTotal() {
        if (rows % limit == 0) {
            return rows / limit;
        } else {
            return rows / limit + 1;
        }
    }

    /**
     * 获取起始页码
     * @return
     */
    public int getFrom() {
        int from = current - 2;
        return Math.max(from,1);
    }

    /**
     * 获取结束页码
     * @return
     */
    public int getTo() {
        int to = current + 2;
        return Math.min(to,getTotal());
    }
}
