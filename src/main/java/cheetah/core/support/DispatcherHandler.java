package cheetah.core.support;

import cheetah.core.AbstractDispatcher;
import cheetah.core.EventMessage;
import cheetah.core.EventResult;
import cheetah.core.NoMapperException;
import cheetah.governor.Governor;
import cheetah.handler.Feedback;
import cheetah.handler.Handler;

import java.util.EventListener;
import java.util.Map;

/**
 * Created by Max on 2016/1/29.
 */
public class DispatcherHandler extends AbstractDispatcher {

    @Override
    public EventResult dispatch() {
        EventMessage eventMessage = context().getEventMessage();
        Map<Class<? extends EventListener>, Handler> handlerMap = context().getHandlers();
        if (!handlerMap.isEmpty()) {
            Governor governor = getEngine().assignGovernor();
            Feedback report = governor.initialize()
                    .setEvent(eventMessage.event())
                    .registerMachineSquad(handlerMap)
                    .setFisrtSucceed(eventMessage.fisrtWin())
                    .setNeedResult(eventMessage.needResult())
                    .command();
            return new EventResult(eventMessage.event().getSource(), report.isFail());
        }
        throw new NoMapperException();
    }

}