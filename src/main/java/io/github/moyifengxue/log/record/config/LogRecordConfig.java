package io.github.moyifengxue.log.record.config;

import io.github.moyifengxue.log.record.aop.LogRecordAop;
import io.github.moyifengxue.log.record.function.CustomFunctionRegistrar;
import io.github.moyifengxue.log.record.service.IOperatorGetService;
import io.github.moyifengxue.log.record.service.impl.DefaultOperatorGetServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 日志记录Starter配置类
 *
 * @author myf
 */
@Configuration
public class LogRecordConfig {

    @Bean
    @ConditionalOnMissingBean(IOperatorGetService.class)
    public IOperatorGetService operatorGetService(){
        return new DefaultOperatorGetServiceImpl();
    }

    @Bean
    public LogRecordAop logRecordAop(IOperatorGetService operatorGetService){
        return new LogRecordAop(operatorGetService);
    }

    @Bean
    public CustomFunctionRegistrar registrar() {
        return new CustomFunctionRegistrar();
    }

}
