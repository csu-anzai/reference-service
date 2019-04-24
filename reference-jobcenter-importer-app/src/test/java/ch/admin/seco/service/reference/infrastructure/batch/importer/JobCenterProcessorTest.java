package ch.admin.seco.service.reference.infrastructure.batch.importer;

import ch.admin.seco.service.reference.infrastructure.batch.importer.config.JobCenterProcessor;
import ch.admin.seco.service.reference.infrastructure.batch.importer.domain.JobCenter;
import ch.admin.seco.service.reference.infrastructure.batch.importer.domain.dto.AddressDTO;
import ch.admin.seco.service.reference.infrastructure.batch.importer.domain.dto.JobCenterDTO;
import ch.admin.seco.service.reference.infrastructure.batch.importer.fixture.JobCenterFixture;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
public class JobCenterProcessorTest {

    private static final String CODE = "TEST01";

    private final JobCenterProcessor jobCenterProcessor = new JobCenterProcessor();

    @Test
    public void testJobCenterProcessor() {
        // given
        JobCenter jobCenter = JobCenterFixture.create(CODE);

        // when
        JobCenterDTO jobCenterDTO = jobCenterProcessor.process(jobCenter);

        // then
        assertThat(jobCenterDTO).isNotNull();
    }

    @Test
    public void testAddressMapping() {
        // given
        JobCenter jobCenter = JobCenterFixture.create(CODE);

        // when
        JobCenterDTO jobCenterDTO = jobCenterProcessor.process(jobCenter);

        // then
        Set<AddressDTO> addresses = jobCenterDTO.getAddresses();
        assertThat(addresses).isNotEmpty();
        assertThat(addresses).hasSize(3);
        addresses.forEach(
            addressDTO -> {
                assertThat(addressDTO.getHouseNumber()).isEqualTo(jobCenter.getHausNr());
                assertThat(addressDTO.getZipCode()).isEqualTo(jobCenter.getPlz());
                // languages can only be : "de", "fr", "it"
                if (addressDTO.getLanguage().equals("de")) {
                    assertThat(addressDTO.getName()).isEqualTo(jobCenter.getNameDe());
                    assertThat(addressDTO.getStreet()).isEqualTo(jobCenter.getStrasseDe());
                    assertThat(addressDTO.getCity()).isEqualTo(jobCenter.getOrtDe());
                } else if (addressDTO.getLanguage().equals("fr")) {
                    assertThat(addressDTO.getName()).isEqualTo(jobCenter.getNameFr());
                    assertThat(addressDTO.getStreet()).isEqualTo(jobCenter.getStrasseFr());
                    assertThat(addressDTO.getCity()).isEqualTo(jobCenter.getOrtFr());
                } else {
                    assertThat(addressDTO.getName()).isEqualTo(jobCenter.getNameIt());
                    assertThat(addressDTO.getStreet()).isEqualTo(jobCenter.getStrasseIt());
                    assertThat(addressDTO.getCity()).isEqualTo(jobCenter.getOrtIt());
                }
            }
        );
    }

    @Test
    public void testCorrectPhoneMapping() {
        // given
        JobCenter jobCenter = JobCenterFixture.createWithPhone(CODE, "0041 79 123 4567");

        // when
        JobCenterDTO jobCenterDTO = jobCenterProcessor.process(jobCenter);

        // then
        assertThat(jobCenterDTO.getPhone()).isNotNull();
        assertThat(jobCenterDTO.getPhone()).isEqualTo("+41791234567");
    }

    @Test
    public void testIncorrectPhoneMapping() {
        // given
        JobCenter jobCenter = JobCenterFixture.createWithPhone(CODE, "0041 999 999");

        // when
        JobCenterDTO jobCenterDTO = jobCenterProcessor.process(jobCenter);

        // then
        assertThat(jobCenterDTO.getPhone()).isNull();
    }

    @Test
    public void testCorrectEmailMapping() {
        // given
        String validEmail = "munster.test@example.com";
        JobCenter jobCenter = JobCenterFixture.createWithEmail(CODE, validEmail);

        // when
        JobCenterDTO jobCenterDTO = jobCenterProcessor.process(jobCenter);

        // then
        assertThat(jobCenterDTO.getEmail()).isNotNull();
        assertThat(jobCenterDTO.getEmail()).isEqualTo(validEmail);
    }

    @Test
    public void testIncorrectEmailMapping() {
        // given
        JobCenter jobCenter = JobCenterFixture.createWithEmail(CODE, "munster.c");

        // when
        JobCenterDTO jobCenterDTO = jobCenterProcessor.process(jobCenter);

        // then
        assertThat(jobCenterDTO.getEmail()).isNull();
    }

    @Test
    public void testShowContactDetails() {
        // given
        String jobCenterCode = "ZGA40";
        JobCenter jobCenter = JobCenterFixture.create(jobCenterCode);

        // when
        JobCenterDTO jobCenterDTO = jobCenterProcessor.process(jobCenter);

        // then
        assertThat(jobCenterDTO.isShowContactDetailsToPublic()).isTrue();
    }

    @Test
    public void testDontShowContactDetails() {
        // given
        String jobCenterCode = "ZGA40N";
        JobCenter jobCenter = JobCenterFixture.create(jobCenterCode);

        // when
        JobCenterDTO jobCenterDTO = jobCenterProcessor.process(jobCenter);

        // then
        assertThat(jobCenterDTO.isShowContactDetailsToPublic()).isFalse();
    }

}
