package ch.admin.seco.service.reference.stream;

import static ch.admin.seco.service.reference.stream.MessageBrokerChannels.REFERENCE_OCCUPATION_LABEL;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Component;

import ch.admin.seco.service.reference.application.dto.CreateOccupationLabelDto;
import ch.admin.seco.service.reference.service.OccupationLabelService;

@Component
@EnableBinding(MessageBrokerChannels.class)
public class OccupationLabelListener {

    private final OccupationLabelService occupationLabelService;

    public OccupationLabelListener(OccupationLabelService occupationLabelService) {
        this.occupationLabelService = occupationLabelService;
    }

    @StreamListener(target = REFERENCE_OCCUPATION_LABEL)
    public void createFromX28(CreateOccupationLabelDto createOccupationLabelDto) {
        occupationLabelService.save(createOccupationLabelDto);
    }
}
