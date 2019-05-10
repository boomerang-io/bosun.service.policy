package net.boomerangplatform.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.boomerangplatform.Application;
import net.boomerangplatform.opa.model.DataRequest;
import net.boomerangplatform.opa.model.DataResponse;
import net.boomerangplatform.opa.service.OpenPolicyAgentClient;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Application.class})
@SpringBootTest
@ActiveProfiles("local")
public abstract class OPAClientTest {

  @Autowired private OpenPolicyAgentClient opaClient;

  @Test
  public void testValidateDataTrue() throws IOException {
    DataRequest dataRequest =
        new ObjectMapper().readValue(getMockFile("dataRequestWhitelistTrue.json"), DataRequest.class);

    DataResponse dataResponse = opaClient.validateData(dataRequest);
    
    System.out.println("Valid: " + dataResponse.getResult().getValid().toString());
    
    assertNotNull(dataResponse);
    assertTrue(dataResponse.getResult().getValid());
  }
  
  @Test
  public void testValidateDataFalse() throws IOException {
    DataRequest dataRequest =
        new ObjectMapper().readValue(getMockFile("dataRequestWhitelistFalse.json"), DataRequest.class);

    DataResponse dataResponse = opaClient.validateData(dataRequest);
    
    System.out.println("Valid: " + dataResponse.getResult().getValid().toString());
    
    assertNotNull(dataResponse);
    assertFalse(dataResponse.getResult().getValid());
  }
  
  @Test
  public void testValidateDataInvalidModel() throws IOException {
    DataRequest dataRequest =
        new ObjectMapper().readValue(getMockFile("dataRequestWhitelistInvalidModel.json"), DataRequest.class);

    DataResponse dataResponse = opaClient.validateData(dataRequest);
    
    System.out.println("Valid: " + dataResponse.getResult().getValid().toString());
    
    assertNotNull(dataResponse);
    assertFalse(dataResponse.getResult().getValid());
  }


  protected String getMockFile(String path) {

    ClassLoader classLoader = getClass().getClassLoader();
    File file = new File(classLoader.getResource(path).getFile());

    String content = "";
    try {
      content = new String(Files.readAllBytes(Paths.get(file.getPath())));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return content;
  }
}
