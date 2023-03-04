package io.github.moyifengxue.log.record.dto;

import lombok.Data;

import java.util.Date;

/**
 * 操作日志DTO
 *
 * @author myf
 */
@Data
public class LogRecordDTO {
    /**
     * 操作用户
     */
    Object operatorUser;
    /**
     * 业务编码
     */
    String bizCode;
    /**
     * 业务ID
     */
    String bizId;
    /**
     * 操作动作
     */
    String msg;
    /**
     * 操作结果
     */
    String result;
    /**
     * 文件ID
     */
    String fileId;
    /**
     * 接口入参
     */
    String param;

    /**
     * 额外信息
     */
    String extra;
    /**
     * 创建时间
     */
    Date createTime;
}
