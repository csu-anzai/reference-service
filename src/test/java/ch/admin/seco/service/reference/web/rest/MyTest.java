package ch.admin.seco.service.reference.web.rest;

import net.minidev.json.JSONArray;
import org.assertj.core.api.Assertions;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class MyTest {

    @Test
    public void dotest() {
        JSONArray array1 = new JSONArray();
        JSONArray array2 = new JSONArray();
        array2.add("aaaa");
        array1.add(array2);

        List<String> expect = Arrays.asList("aaaa");

        MatcherAssert.assertThat(array1, Matchers.hasItem(Matchers.equalTo(expect)));
    }
}
