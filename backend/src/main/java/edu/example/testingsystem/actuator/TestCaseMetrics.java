package edu.example.testingsystem.actuator;

import edu.example.testingsystem.entities.TestCase;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.data.rest.core.event.AbstractRepositoryEventListener;
import org.springframework.stereotype.Component;

/**
 * Ведет статистику созданных тест-кейсов для метрик акутатора(/metrics)
 * Почему-то временно не работает
 */
@Component
public class TestCaseMetrics extends AbstractRepositoryEventListener<TestCase> {
    private MeterRegistry meterRegistry;

    public TestCaseMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    protected void onAfterCreate(TestCase testCase) {
        System.out.println(testCase);
        meterRegistry.counter("testcases", "created").increment();
    }
}