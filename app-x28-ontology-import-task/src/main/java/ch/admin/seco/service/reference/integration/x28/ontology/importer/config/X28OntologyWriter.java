package ch.admin.seco.service.reference.integration.x28.ontology.importer.config;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;

import ch.admin.seco.service.reference.application.dto.CreateOccupationLabelDto;

public class X28OntologyWriter implements ItemWriter<CreateOccupationLabelDto> {
    private final MessageChannel output;

    public X28OntologyWriter(MessageChannel output) {
        this.output = output;
    }

    @Override
    public void write(List<? extends CreateOccupationLabelDto> createOccupationLabelDtos) {
        createOccupationLabelDtos.forEach(this::send);
    }

    public void send(CreateOccupationLabelDto createOccupationLabelDto) {
        output.send(MessageBuilder
                .withPayload(createOccupationLabelDto)
                .setHeader("action", "import")
                .setHeader("sourceSystem", "X28")
                .setHeader("sourceSystem", "referenceservice")
                .build());
    }
}
