package ch.admin.seco.service.reference.infrastructure.batch.importer.fixture;

import ch.admin.seco.service.reference.infrastructure.batch.importer.domain.JobCenter;

public class JobCenterFixture {

    private static final String SUFFIX = "test";

    public static JobCenter create(String code, String suffix) {
        JobCenter jobCenter = new JobCenter();
        jobCenter.setCode(code);

        jobCenter.setNameDe("NAME_DE_" + suffix);
        jobCenter.setNameFr("NAME_FR_" + suffix);
        jobCenter.setNameIt("NAME_IT_" + suffix);

        jobCenter.setStrasseDe("STRASSE_DE_" + suffix);
        jobCenter.setStrasseFr("STRASSE_FR_" + suffix);
        jobCenter.setStrasseIt("STRASSE_IT_" + suffix);

        jobCenter.setOrtDe("ORT_DE_" + suffix);
        jobCenter.setOrtFr("ORT_FR_" + suffix);
        jobCenter.setOrtIt("ORT_IT_" + suffix);

        jobCenter.setHausNr("HAUS_NR_" + suffix);
        jobCenter.setPlz("PLZ_" + suffix);

        jobCenter.setTelefon("0041799999999");
        jobCenter.setFax("0041799999999");
        jobCenter.setEmail("email@example.org");

        return jobCenter;
    }

    public static JobCenter create(String code) {
        return create(code, SUFFIX);
    }

    public static JobCenter createWithPhone(String code, String phone) {
        JobCenter jobCenter = create(code, SUFFIX);
        jobCenter.setTelefon(phone);

        return jobCenter;

    }

    public static JobCenter createWithEmail(String code, String email) {
        JobCenter jobCenter = create(code, SUFFIX);
        jobCenter.setEmail(email);

        return jobCenter;
    }
}
