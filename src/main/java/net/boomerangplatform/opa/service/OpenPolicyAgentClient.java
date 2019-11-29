package net.boomerangplatform.opa.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import net.boomerangplatform.opa.exception.OPAClientException;
import net.boomerangplatform.opa.model.DataRequest;
import net.boomerangplatform.opa.model.DataResponse;

@Component
public class OpenPolicyAgentClient {

  private static final Logger LOGGER = LogManager.getLogger();

  @Value("${opa.rest.url.base}")
  public String opaBaseUrl;

  @Value("${opa.rest.url.data}")
  public String opaRestDataUrl;

  @Autowired
  private RestTemplate restTemplate;

  public DataResponse validateData(DataRequest dataRequest) {

    final HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type", "application/json");
    final String url =
        opaBaseUrl + opaRestDataUrl + "/" + dataRequest.getInput().getPolicy().getKey();

    final HttpEntity<DataRequest> request = new HttpEntity<>(dataRequest, headers);
    ResponseEntity<DataResponse> response = null;
    try {
      response = restTemplate.exchange(url, HttpMethod.POST, request, DataResponse.class);
    } catch (final RestClientException e) {
      LOGGER.error(e.getMessage(), e);
    }

    if (response == null) {
      throw new OPAClientException("Exception or missing response from OPA!");
    }

    return response.getBody();
  }
}
