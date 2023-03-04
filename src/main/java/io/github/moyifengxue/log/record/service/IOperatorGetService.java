package io.github.moyifengxue.log.record.service;

import io.github.moyifengxue.log.record.dto.LogRecordDTO;

/**
 * 获取操作用户接口
 *
 * @author zgx
 */
public interface IOperatorGetService {

    /**
     * 获取登录用户
     *
     * @return 用户
     */
    Object getUser();

    /**
     * 后续处理日志记录
     *
     * @param logRecordDTO 日志记录实体
     */
    void insertLogRecord(LogRecordDTO logRecordDTO);

    /**
     * 自定义通知返回值错误信息解析
     *
     * @param result result
     * @return errMsg
     */
    String customResponseError(Object result);

    /**
     * 入参过滤
     * @param args 方法参数列表
     *
     * @return 入参过滤
     */
    String paramFilter(Object[] args);

}
