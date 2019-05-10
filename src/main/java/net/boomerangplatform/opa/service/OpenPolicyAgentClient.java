package net.boomerangplatform.opa.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import net.boomerangplatform.opa.model.DataRequest;
import net.boomerangplatform.opa.model.DataResponse;

@Component
public class OpenPolicyAgentClient {

  private final Logger logger = LogManager.getLogger();

  @Value("${opa.rest.url.base}")
  public String opaBaseUrl;

  @Value("${opa.rest.url.data}")
  public String opaRestDataUrl;

  @Autowired
  @Qualifier("internalRestTemplate")
  private RestTemplate restTemplate;

  public ResponseEntity<DataResponse> validateData(DataRequest dataRequest) {

    final HttpHeaders headers = new HttpHeaders();
    headers.add("Accept", "application/json");
    final String url = opaBaseUrl + opaRestDataUrl + "/" + dataRequest.getInput().getPolicy().getKey();

    final HttpEntity<DataRequest> request = new HttpEntity<DataRequest>(dataRequest, headers);
    ResponseEntity<DataResponse> response = null;
    try {
      response = restTemplate.exchange(url, HttpMethod.POST, request, DataResponse.class);
    } catch (final Exception e) {
      logger.error(e.getMessage(), e);
    }
    return response;
  }
}
