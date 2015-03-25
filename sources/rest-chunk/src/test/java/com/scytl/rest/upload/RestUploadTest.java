package com.scytl.rest.upload;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.notNullValue;

import java.net.URI;
import java.net.URL;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.UriBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.arquillian.warp.Inspection;
import org.jboss.arquillian.warp.Warp;
import org.jboss.arquillian.warp.WarpTest;
import org.jboss.arquillian.warp.servlet.AfterServlet;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@WarpTest
public class RestUploadTest {

	@Deployment
	public static final WebArchive deployment() {
		return ShrinkWrap
				.create(WebArchive.class)
				.addClasses(BoundedInputStream.class, IOUtil.class,
						P12Resource.class, TemporaryFolder.class)
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
				.addAsLibraries(
						Maven.resolver().loadPomFromFile("pom.xml")
								.resolve("org.glassfish:javax.json")
								.withTransitivity().as(JavaArchive.class));
	}

	@ArquillianResource
	URL baseUrl;

	@Test
	@RunAsClient
	public void shouldUploadContent() {
		Warp.initiate(() -> {

			URI endpoint = UriBuilder.fromPath(baseUrl.toExternalForm())
					.path(P12Resource.class).path("chunkedUpload").build();
			given().request().body("This is a test file.".getBytes())
					.post(endpoint).then().assertThat()
					.body("uploadId", notNullValue()).and()
					.body("offset", notNullValue());

		}).inspect(new Inspection() {
			private static final long serialVersionUID = 1L;
			
			@Inject
			TemporaryFolder temporaryFolder;
			
			@AfterServlet
			public void assertCreatedFile() {
				System.out.println(temporaryFolder.getRoot());
			}
			
		});

	}

}
