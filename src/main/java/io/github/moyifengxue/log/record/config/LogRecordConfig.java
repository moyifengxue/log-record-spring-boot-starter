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

    /**
     * 扩展接口
     * @return 扩展接口实现类
     */
    @Bean
    @ConditionalOnMissingBean(IOperatorGetService.class)
    public IOperatorGetService operatorGetService(){
        return new DefaultOperatorGetServiceImpl();
    }

    /**
     * AOP
     * @param operatorGetService 扩展接口
     * @return AOP
     */
    @Bean
    public LogRecordAop logRecordAop(IOperatorGetService operatorGetService){
        return new LogRecordAop(operatorGetService);
    }

    /**
     * 自定义函数注册类
     * @return 自定义函数
     */
    @Bean
    public CustomFunctionRegistrar registrar() {
        return new CustomFunctionRegistrar();
    }

}
