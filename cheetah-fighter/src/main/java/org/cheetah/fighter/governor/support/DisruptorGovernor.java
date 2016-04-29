package org.cheetah.fighter.governor.support;

import org.cheetah.fighter.async.disruptor.DisruptorEvent;
import org.cheetah.fighter.governor.AbstractGovernor;
import org.cheetah.fighter.handler.Feedback;
import org.cheetah.fighter.worker.Command;
import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;

/**
 * Created by Max on 2016/2/29.
 */
public class DisruptorGovernor extends AbstractGovernor {
    private RingBuffer<DisruptorEvent> ringBuffer;

    @Override
    protected Feedback notifyAllWorker() {
        if (handlerMap().isEmpty())
            return Feedback.EMPTY;
        Translator translator = new Translator();
        this.handlerMap().keySet().forEach(c -> {
            Command command = Command.of(details().event(), details().callback(), c);
            ringBuffer.publishEvent(translator, command);
        });

        return Feedback.SUCCESS;
    }

    public DisruptorGovernor setRingBuffer(RingBuffer<DisruptorEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
        return this;
    }

    static class Translator implements EventTranslatorOneArg<DisruptorEvent, Command> {
        @Override
        public void translateTo(DisruptorEvent event, long sequence, Command data) {
            event.set(data);
        }
    }
}
