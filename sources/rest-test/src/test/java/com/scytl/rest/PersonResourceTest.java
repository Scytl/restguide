package com.scytl.rest;

import static com.jayway.restassured.RestAssured.get;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;


import com.jayway.restassured.path.json.JsonPath;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.UriBuilder;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

@RunWith(Arquillian.class)
public class PersonResourceTest {

    @Deployment(testable = false)
    public static WebArchive createDeployment() {
            return ShrinkWrap.create(WebArchive.class, "persontest.war")
                    .addClass(PersonResource.class);
    }

    @ArquillianResource
    URL baseUrl;

    @Test
    public void shouldReturnExpectedPerson() {
        URI endpoint = UriBuilder.fromPath(baseUrl.toExternalForm()).path(PersonResource.class).path("1").build();
        InputStream response = get(endpoint).then().statusCode(200).extract().asInputStream();

        JsonPath jsonPath = new JsonPath(response);
        String firstName = jsonPath.getString("firstName");
        String streetAddress = jsonPath.getString("address.streetAddress");

        assertThat(firstName, is("John"));
        assertThat(streetAddress, is("21 2nd Street"));
    }

}
