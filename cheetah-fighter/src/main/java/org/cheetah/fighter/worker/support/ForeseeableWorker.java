package org.cheetah.fighter.worker.support;

import org.cheetah.common.logger.Info;
import org.cheetah.common.logger.Warn;
import org.cheetah.common.utils.Objects;
import org.cheetah.fighter.Feedback;
import org.cheetah.fighter.Interceptor;
import org.cheetah.fighter.handler.Handler;
import org.cheetah.fighter.worker.AbstractWorker;
import org.cheetah.fighter.worker.Command;

import java.util.List;
import java.util.concurrent.*;

/**
 * Created by Max on 2016/3/2.
 */
public class ForeseeableWorker extends AbstractWorker {
    private ExecutorService executor;

    public ForeseeableWorker(Handler handler, List<Interceptor> interceptors) {
        super(handler, interceptors);
    }

    /**
     * 根据接受到命令开始工作
     *
     * @param command
     */
    @Override
    public Feedback work(Command command) {
        try {
            long start = System.nanoTime();
            CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> {
                if(command.needResult())
                    invoke(command);
                else
                    try {
                        invoke(command);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Warn.log(this.getClass(), "Failure events consumption", e);
                        throw e;
                    }
                return true;
            }, executor).whenComplete((r, e) -> {
                if (Objects.nonNull(r) && r)
                    handler.onSuccess(command);
                else handler.onFailure(command, e);
            });

            if (command.needResult())
                if (command.timeLimit())
                    future.get(command.timeout(), command.timeUnit());
                else
                    future.get();

            if (Info.isEnabled(this.getClass())) {
                Info.log(this.getClass(), handler.getEventListener().getClass().getName() + " execution time : {}", (System.nanoTime() - start) + " ns");
            }
            return Feedback.SUCCESS;
        } catch (RejectedExecutionException e) {
            e.printStackTrace();
            Warn.log(getClass(), "event rejected execute.", e);
            handler.onFailure(command, e);
            return Feedback.failure(e, handler.getEventListener().getClass());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            Warn.log(getClass(), "event consumer execution error.", e);
            handler.onFailure(command, e);
            return Feedback.failure(e, handler.getEventListener().getClass());
        } catch (TimeoutException e) {
            e.printStackTrace();
            Warn.log(getClass(), "event consumer execution timeout.", e);
            return Feedback.failure(e, handler.getEventListener().getClass());
        }
    }

    @Override
    protected boolean doWork(Command command) {
        return handler.handle(command);
    }

    @Override
    public List<Interceptor> getInterceptors() {
        return interceptors;
    }

    public void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

}
