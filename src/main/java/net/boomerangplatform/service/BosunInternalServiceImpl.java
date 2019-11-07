package net.boomerangplatform.service;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.boomerangplatform.entity.PolicyDefinitionEntity;
import net.boomerangplatform.exception.BosunException;
import net.boomerangplatform.repository.PolicyDefinitionRepository;

@Service
public class BosunInternalServiceImpl implements BosunInternalService {

  @Autowired private PolicyDefinitionRepository policyDefinitionRepository;

  private static final Logger LOGGER = LogManager.getLogger();

  @Override
  public ByteArrayOutputStream getBundle() {
    TarArchiveOutputStream taos = null;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    List<PolicyDefinitionEntity> entities = policyDefinitionRepository.findAll();

    try {
      taos = new TarArchiveOutputStream(new GZIPOutputStream(new BufferedOutputStream(baos)));

      for (PolicyDefinitionEntity entity : entities) {
        String definitionName = entity.getKey() + ".rego";
        LOGGER.info("Adding definition: " + definitionName + "...");
        TarArchiveEntry tae = new TarArchiveEntry(definitionName);

        byte[] definitionBytes = Base64.getDecoder().decode(entity.getRego());
//        byte[] definitionBytes = entity.getRego().getBytes("UTF-8");
        LOGGER.info(new String(definitionBytes, "UTF-8"));
        tae.setSize(definitionBytes.length);
        taos.putArchiveEntry(tae);
        // The write command allows you to write bytes to the current entry
        // on the output stream. It will not allow you to write any more than the size
        // that you specified when you created the archive entry above
        taos.write(definitionBytes);
        taos.closeArchiveEntry();
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new BosunException(e.getMessage());
    } finally {
      try {      
        taos.close();
      } catch (IOException e) {
        e.printStackTrace();
        throw new BosunException(e.getMessage());
      }
    }

    return baos;
  }
}
