package io.github.moyifengxue.log.record.service.impl;

import io.github.moyifengxue.log.record.dto.LogRecordDTO;
import io.github.moyifengxue.log.record.result.Result;
import io.github.moyifengxue.log.record.service.IOperatorGetService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;


/**
 * 默认实现
 *
 * @author zgx
 */
public class DefaultOperatorGetServiceImpl implements IOperatorGetService {
    @Override
    public Object getUser() {
        // 获取上下文用户信息逻辑
        return new Object();
    }

    @Override
    public void insertLogRecord(LogRecordDTO logRecordDTO) {
        // 保存日志信息
        // 可以在此处从日志上下文获取数据
    }

    @Override
    public String customResponseError(Object result) {
        String message = "";
        if (result instanceof Result<?>) {
            if (!((Result<?>) result).isSuccess()) {
                message = ((Result<?>) result).getMessage();
            }
        }
        return message;
    }

    @Override
    public String paramFilter(Object[] args) {
        List<Object> objectList = Arrays.stream(args)
                .filter(arg -> !((arg instanceof MultipartFile) || (arg instanceof MultipartFile[]) ||
                        (arg instanceof HttpServletRequest) || (arg instanceof HttpServletResponse) ||
                        (arg instanceof Principal)))
                .toList();
        return objectList.toString();
    }
}
