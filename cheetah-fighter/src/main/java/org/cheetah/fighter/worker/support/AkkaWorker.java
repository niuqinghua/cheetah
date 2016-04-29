package org.cheetah.fighter.worker.support;

import akka.actor.OneForOneStrategy;
import akka.actor.SupervisorStrategy;
import akka.actor.UntypedActor;
import org.cheetah.fighter.async.akka.ActorFactory;
import org.cheetah.commons.logger.Debug;
import org.cheetah.commons.utils.Assert;
import org.cheetah.fighter.core.Interceptor;
import org.cheetah.fighter.handler.Feedback;
import org.cheetah.fighter.handler.Handler;
import org.cheetah.fighter.worker.Command;
import org.cheetah.fighter.worker.Worker;
import org.cheetah.fighter.handler.Directive;
import scala.Option;
import scala.concurrent.duration.Duration;

import java.util.EventListener;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by Max on 2016/2/21.
 */
public class AkkaWorker extends UntypedActor implements Worker {
    private Map<Class<? extends EventListener>, Handler> eventlistenerMapper;
    private List<Interceptor> interceptors;

    public AkkaWorker(Map<Class<? extends EventListener>, Handler> eventlistenerMapper) {
        this.eventlistenerMapper = eventlistenerMapper;
    }

    public AkkaWorker() {

    }

    @Override
    public void doWork(Command command) {
        try {
            Assert.notNull(command, "order must not be null");
            Handler machine = eventlistenerMapper.get(command.eventListener());
            Feedback feedback = machine.handle(new Directive(command.event(), command.callback(), command.needResult()));
//            getSender().tell(feedback, getSelf());
        } catch (Exception e) {
            Debug.log(this.getClass(), "machine execute fail.", e);
            getSender().tell(Feedback.FAILURE, getSelf());
        }
    }

    @Override
    public List<Interceptor> getInterceptors() {
        return this.interceptors;
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message.equals(ActorFactory.STATUS_CHECK_MSG)) {
            getSender().tell(ActorFactory.STATUS_OK, getSelf());
        } else if (message instanceof Command) {
            work((Command) message);
        } else unhandled(message);
    }

    @Override
    public void preStart() throws Exception {
        Debug.log(getClass(), "worker actor =>" + getSelf().path() + "-----> start");
    }

    @Override
    public void postStop() throws Exception {
        Debug.log(getClass(), "worker actor =>" + getSelf().path() + "-----> postStop");
    }

    @Override
    public void preRestart(Throwable reason, Option<Object> message) throws Exception {
        Debug.log(getClass(), "worker actor =>" + getSelf().path() + "-----> preRestart");
    }

    @Override
    public void postRestart(Throwable reason) throws Exception {
        Debug.log(getClass(), "worker actor =>" + getSelf().path() + "-----> postRestart");
    }

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return new OneForOneStrategy(3, Duration.create(1, TimeUnit.SECONDS), param -> {
            if (param instanceof ArithmeticException) {
                return SupervisorStrategy.resume();
            } else if (param instanceof NullPointerException) {
                return SupervisorStrategy.restart();
            } else if (param instanceof IllegalArgumentException) {
                return SupervisorStrategy.restart();
            } else return SupervisorStrategy.escalate();
        });
    }
}
