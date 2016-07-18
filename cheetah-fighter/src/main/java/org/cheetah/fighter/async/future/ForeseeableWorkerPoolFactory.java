package org.cheetah.fighter.async.future;

import org.cheetah.fighter.async.AsynchronousFactory;
import org.cheetah.fighter.async.AsynchronousPoolFactory;
import org.cheetah.fighter.core.EventContext;
import org.cheetah.fighter.core.NoMapperException;
import org.cheetah.fighter.core.HandlerMapping;
import org.cheetah.fighter.worker.ForeseeableWorker;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Max on 2016/2/29.
 */
public class ForeseeableWorkerPoolFactory implements AsynchronousPoolFactory<ForeseeableWorker[]> {
    private AsynchronousFactory<ForeseeableWorker[]> asynchronousFactory;
    private final Map<HandlerMapping.HandlerMapperKey, ForeseeableWorker[]> workerPool;
    private EventContext context;

    public ForeseeableWorkerPoolFactory() {
        this.workerPool = new ConcurrentHashMap<>();
    }

    public ForeseeableWorker[] createWorkerTeam() {
        ForeseeableWorker[] workers = this.workerPool.get(HandlerMapping.HandlerMapperKey.generate(context.eventMessage().event()));
        if(Objects.nonNull(workers))
            return workers;
        return  this.asynchronousFactory.createAsynchronous(context.eventMessage().event().getClass().getName(),
                context.handlers(), context.interceptors());
    }

    @Override
    public ForeseeableWorker[] getAsynchronous() {
        ForeseeableWorker[] workers = this.workerPool.get(HandlerMapping.HandlerMapperKey.generate(context.eventMessage().event()));
        if (Objects.nonNull(workers)) {
            return workers;
        } else {
            synchronized (this) {
                if(context.handlers().isEmpty())
                    throw new NoMapperException();
                workers = createWorkerTeam();
                HandlerMapping.HandlerMapperKey key = HandlerMapping.HandlerMapperKey.generate(context.eventMessage().event());
                this.workerPool.putIfAbsent(key, workers);
                return workers;
            }
        }
    }


    @Override
    public void setEventContext(EventContext context) {
        this.context = context;
    }

    @Override
    public void setAsynchronousFactory(AsynchronousFactory asynchronousFactory) {
        this.asynchronousFactory = asynchronousFactory;
    }

    @Override
    public void start() {
        this.asynchronousFactory.start();
    }

    @Override
    public void stop() {
        this.workerPool.clear();
        this.asynchronousFactory.stop();
    }

}