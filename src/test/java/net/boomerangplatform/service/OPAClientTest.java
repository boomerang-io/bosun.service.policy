package net.boomerangplatform.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.annotation.Resource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.boomerangplatform.Application;
import net.boomerangplatform.opa.exception.OPAClientException;
import net.boomerangplatform.opa.model.DataRequest;
import net.boomerangplatform.opa.model.DataResponse;
import net.boomerangplatform.opa.model.DataResponseResult;
import net.boomerangplatform.opa.service.OpenPolicyAgentClient;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {Application.class})
@SpringBootTest
@ActiveProfiles("test")
public class OPAClientTest {

  @Autowired
  private OpenPolicyAgentClient opaClient;

  @Autowired
  @Qualifier("internalRestTemplate")
  @Resource(name = "internalRestTemplate")
  protected RestTemplate restTemplate;

  protected MockRestServiceServer server;


  private static final String opaURL = "http://localhost:8181/v1/data/citadel/";

  @Before
  public void setUp() {
    this.server = MockRestServiceServer.createServer(restTemplate);
  }

  @Test
  public void testValidateData() throws IOException {
    DataRequest dataRequest = new ObjectMapper()
        .readValue(getMockFile("dataRequestWhitelistTrue.json"), DataRequest.class);

    String key = dataRequest.getInput().getPolicy().getKey();
    String url = opaURL + key;

    this.server.expect(requestTo(url))
        .andRespond(withSuccess(parseToJson(getDataResponse()), MediaType.APPLICATION_JSON));

    DataResponse dataResponse = opaClient.validateData(dataRequest);

    assertNotNull(dataResponse);
    assertTrue(dataResponse.getResult().getValid());

    this.server.verify();
  }

  @Test(expected = OPAClientException.class)
  public void testValidateDataWithNullResponse() throws IOException {
    DataRequest dataRequest = new ObjectMapper()
        .readValue(getMockFile("dataRequestWhitelistFalse.json"), DataRequest.class);

    String key = dataRequest.getInput().getPolicy().getKey();
    String url = opaURL + key;

    this.server.expect(requestTo(url)).andRespond(withBadRequest());

    opaClient.validateData(dataRequest);

    this.server.verify();
  }

  private DataResponse getDataResponse() {
    DataResponse dataResponse = new DataResponse();
    DataResponseResult result = new DataResponseResult();

    final ObjectMapper mapper = new ObjectMapper();
    JsonNode node = mapper.createObjectNode();
    result.setDetail(node);
    result.setValid(true);

    dataResponse.setResult(result);

    return dataResponse;
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

  protected String parseToJson(final Object template) throws JsonProcessingException {
    final ObjectMapper mapper = new ObjectMapper();
    return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(template);
  }
}
