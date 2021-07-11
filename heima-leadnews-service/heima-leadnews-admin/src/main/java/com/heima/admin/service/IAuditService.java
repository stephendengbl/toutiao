package com.heima.admin.service;

public interface IAuditService {
    /**
     * 根据自媒体文章id审核文章
     * @param id
     */
    public void auditById(Integer id);
}
