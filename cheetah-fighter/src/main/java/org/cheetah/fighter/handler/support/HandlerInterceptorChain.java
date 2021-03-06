package org.cheetah.fighter.handler.support;

import org.cheetah.commons.logger.Err;
import org.cheetah.commons.utils.CollectionUtils;
import org.cheetah.fighter.Interceptor;
import org.cheetah.fighter.worker.Command;

import java.util.ArrayList;
import java.util.List;

/**
 * 拦截器链
 * Created by Max on 2016/3/7.
 */
public class HandlerInterceptorChain implements Cloneable {
    private List<Interceptor> interceptors;
    private int interceptorIndex = -1;
    private static HandlerInterceptorChain DEFAULT_CHAIN = new HandlerInterceptorChain();
    /**
     *  消费者执行前，先调用所有拦截器的prehandle
     * @param command
     * @return
     */
    public boolean beforeHandle(Command command) throws Exception {
        List<Interceptor> $interceptors = getInterceptors();
        if (!CollectionUtils.isEmpty($interceptors)) {
            for (int i = 0; i < $interceptors.size(); i++) {
                if (!$interceptors.get(i).preHandle(command)) {
                    this.triggerAfterCompletion(command, null);
                    return false;
                }
                this.interceptorIndex = i;
            }
        }
        return true;
    }
    /**
     *  消费者执行完毕后，先调用所有拦截器的prehandle
     * @param command
     */
    public void afterHandle(Command command) throws Exception {
        List<Interceptor> $interceptors = getInterceptors();
        if (!CollectionUtils.isEmpty($interceptors)) {
            for(int i = $interceptors.size() - 1; i >= 0; i--) {
                $interceptors.get(i).postHandle(command);
            }
        }
    }
    /**
     * prehandle执行返回false时执行触发方法， 如果消费者执行异常也会触发一下方法
     * @param command
     * @param ex
     */
    public void triggerAfterCompletion(Command command, Exception ex) {
        List<Interceptor> $interceptors = getInterceptors();
        if (!CollectionUtils.isEmpty($interceptors)) {
            for (int i = this.interceptorIndex; i >= 0; i--) {
                try {
                    $interceptors.get(i).afterCompletion(command, ex);
                } catch (Exception e) {
                    Err.log(this.getClass(), "InterceptorChain.afterCompletion threw exception");
                }
            }
        }
    }

    /**
     * 注册拦截器
     * @param interceptor
     */
    public void register(Interceptor interceptor) {
        interceptors.add(interceptor);
    }

    List<Interceptor> getInterceptors() {
        return interceptors;
    }

    public void initialize() {
        this.interceptorIndex = -1;
        this.interceptors = new ArrayList<>();
    }

    public void reset() {
        this.interceptors = null;
        this.interceptorIndex = -1;
    }

    public static HandlerInterceptorChain getDefualtChain() {
        return DEFAULT_CHAIN;
    }

    public HandlerInterceptorChain kagebunsin() throws CloneNotSupportedException {
        HandlerInterceptorChain chain= (HandlerInterceptorChain) super.clone();
        chain.reset();
        chain.initialize();
        return chain;
    }

    public static HandlerInterceptorChain createChain() throws CloneNotSupportedException {
        return DEFAULT_CHAIN.kagebunsin();
    }
}
