package ch.admin.seco.service.reference.infrastructure.batch.importer.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepListenerSupport;
import org.springframework.batch.core.step.skip.SkipException;

public class SaveSkippedItemListener extends StepListenerSupport {
    private static final Logger LOGGER = LoggerFactory.getLogger(SaveSkippedItemListener.class);

    private ThreadLocal<StepExecution> stepExecutionThreadLocal = new ThreadLocal<>();

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        stepExecutionThreadLocal.remove();
        return null;
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        stepExecutionThreadLocal.set(stepExecution);
    }

    @Override
    public void onSkipInWrite(Object item, Throwable t) {
        LOGGER.error(String.format("Item skipped: %s - %s", item, t.getMessage()));
        stepExecutionThreadLocal.get().addFailureException(new SkippedItemException(item, t));
    }
}

class SkippedItemException extends SkipException {
    SkippedItemException(Object msg, Throwable nested) {
        super(msg.toString(), nested);
    }

    @Override
    public String toString() {
        return "SkippedItemException{" +
                "cause=" + getCause() +
                ", message='" + getLocalizedMessage() + '\'' +
                "}";
    }
}
