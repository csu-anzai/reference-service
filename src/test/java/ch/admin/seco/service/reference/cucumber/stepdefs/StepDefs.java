package ch.admin.seco.service.reference.cucumber.stepdefs;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;

import ch.admin.seco.service.reference.ReferenceserviceApp;

@WebAppConfiguration
@SpringBootTest
@ContextConfiguration(classes = ReferenceserviceApp.class)
public abstract class StepDefs {

    protected ResultActions actions;

}
