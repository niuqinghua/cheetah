package org.cheetah.fighter.async;

import org.cheetah.commons.Startable;
import org.cheetah.fighter.EventContext;

/**
 * 异步工作者池子的工厂
 * Created by Max on 2016/2/29.
 */
public interface AsynchronousPoolFactory<T> extends Startable {
    /**
     * 获取一个异步工作者
     * @return
     */
    T getAsynchronous();

    /**
     * 设置事件的映射器
     * @param context
     */
    void setEventContext(EventContext context);

    void setAsynchronousFactory(AsynchronousFactory asynchronousFactory);
}
