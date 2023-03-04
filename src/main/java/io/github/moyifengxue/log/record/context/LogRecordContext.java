package io.github.moyifengxue.log.record.context;

import org.springframework.core.NamedThreadLocal;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * 操作日志上下文
 *
 * @author zgx
 */
public class LogRecordContext {

    public static final String CONTEXT_KEY_NAME_FILE_ID = "fileId";
    public static final String CONTEXT_KEY_NAME_BATCH_ID = "batchId";
    private static final ThreadLocal<StandardEvaluationContext> CONTEXT_THREAD_LOCAL = new NamedThreadLocal<>("ThreadLocal StandardEvaluationContext");

    public static StandardEvaluationContext getContext() {
        return CONTEXT_THREAD_LOCAL.get() == null ? new StandardEvaluationContext() : CONTEXT_THREAD_LOCAL.get();
    }

    /**
     * 上下文存储数据
     * @param key key
     * @param value value
     */
    public static void putVariable(String key, Object value) {
        StandardEvaluationContext context = getContext();
        context.setVariable(key, value);
        CONTEXT_THREAD_LOCAL.set(context);
    }

    /**
     * 获取上下文中数据
     * @param key key
     * @return value
     */
    public static Object getVariable(String key) {
        StandardEvaluationContext context = getContext();
        return context.lookupVariable(key);
    }

    /**
     * Removes the current thread's value for this thread-local variable.
     */
    public static void clearContext() {
        CONTEXT_THREAD_LOCAL.remove();
    }

}
