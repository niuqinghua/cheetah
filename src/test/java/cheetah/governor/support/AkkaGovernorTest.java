package cheetah.governor.support;

import cheetah.client.ApplicationEventEmitter;
import cheetah.client.DomainEventEmitter;
import cheetah.domain.Entity;
import cheetah.domain.UUIDKeyEntity;
import cheetah.event.*;
import cheetah.util.ArithUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Max on 2016/2/24.
 */
@ContextConfiguration("classpath:META-INF/application.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class AkkaGovernorTest {

    public static final AtomicLong atomicLong = new AtomicLong();

    @Test
    public void launch2() throws InterruptedException {

        while (true) {
            ApplicationEventEmitter.launch(
                    new ApplicationEventTest("213")
            );
        }

    }


    @Test
    public void launch() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                while (true) {
                    ApplicationEventEmitter.launch(
                            new ApplicationEventTest("213")
                    );
                }
            }).start();
        }
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                while (true) {
                    DomainEventEmitter.launch(
                            new DomainEventTest(new User("huahng"))
                    );
                }
            }).start();
        }
        latch.await();
    }

    public static class ApplicationEventTest extends ApplicationEvent {

        /**
         * Constructs a prototypical Event.
         *
         * @param source The object on which the Event initially occurred.
         * @throws IllegalArgumentException if source is null.
         */
        public ApplicationEventTest(Object source) {
            super(source);
        }
    }

    public static class DomainEventTest extends DomainEvent {

        /**
         * Constructs a prototypical Event.
         *
         * @param source The object on which the Event initially occurred.
         * @throws IllegalArgumentException if source is null.
         */
        public DomainEventTest(Object source) {
            super(source);
        }
    }

    public static class ApplicationListenerTest implements ApplicationListener<ApplicationEventTest> {
        @Override
        public void onApplicationEvent(ApplicationEventTest event) {
            double v = ArithUtil.round(Math.random() * 100, 0);
            long i = ArithUtil.convertsToLong(v);
            try {
                Thread.sleep(i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(atomicLong.incrementAndGet());
        }
    }

    public static class SmartApplicationListenerTest implements SmartApplicationListener<ApplicationEventTest> {
        @Override
        public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
            return ApplicationEventTest.class == eventType;
        }

        @Override
        public boolean supportsSourceType(Class<?> sourceType) {
            return String.class == sourceType;
        }

        @Override
        public int getOrder() {
            return 0;
        }

        @Override
        public void onApplicationEvent(ApplicationEventTest event) {
            double v = ArithUtil.round(Math.random() * 100, 0);
            long i = ArithUtil.convertsToLong(v);
            try {
                Thread.sleep(i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("SmartApplicationListenerTest -- " + atomicLong.incrementAndGet());
        }
    }

    public static class SmartDomainListenerTest implements SmartDomainEventListener<DomainEventTest> {

        @Override
        public boolean supportsEventType(Class<? extends DomainEvent> eventType) {
            return eventType == DomainEventTest.class;
        }

        @Override
        public boolean supportsSourceType(Class<? extends Entity> sourceType) {
            return User.class == sourceType;
        }

        @Override
        public int getOrder() {
            return 0;
        }

        @Override
        public void onDomainEvent(DomainEventTest event) {
            double v = ArithUtil.round(Math.random() * 100, 0);
            long i = ArithUtil.convertsToLong(v);
            try {
                Thread.sleep(i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("DomainEventTest -- " + atomicLong.incrementAndGet());
        }
    }

    public static class User extends UUIDKeyEntity {

        private static final long serialVersionUID = -2269393138381549806L;
        private String name;

        public User(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "User{" +
                    "name='" + name + '\'' +
                    "} " + super.toString();
        }
    }
}