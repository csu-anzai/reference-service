package ch.admin.seco.service.reference.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface MessageBrokerChannels {
    String REFERENCE_OCCUPATION_LABEL = "reference-occupation-label";

    @Input(REFERENCE_OCCUPATION_LABEL)
    SubscribableChannel referenceOccuplationLabelChannel();
}
