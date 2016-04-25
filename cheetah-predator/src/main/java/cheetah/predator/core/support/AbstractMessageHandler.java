package cheetah.predator.core.support;

import cheetah.predator.core.Interceptor;
import cheetah.predator.core.Message;
import cheetah.predator.core.MessageType;
import cheetah.predator.core.Session;

/**
 * Created by Max on 2016/3/26.
 */
public class AbstractMessageHandler implements Interceptor {
    @Override
    public boolean handle(Message message, Session session) throws Exception {
        return false;
    }

    @Override
    public void afterCompletion(Message message, Session session, Exception ex) throws Exception {

    }

    @Override
    public boolean supportsType(MessageType type) {
        return false;
    }
}