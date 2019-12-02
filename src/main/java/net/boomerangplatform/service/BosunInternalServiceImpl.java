package net.boomerangplatform.service;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import net.boomerangplatform.entity.PolicyTemplateEntity;
import net.boomerangplatform.exception.BosunException;
import net.boomerangplatform.repository.PolicyTemplateRepository;

@Service
public class BosunInternalServiceImpl implements BosunInternalService {

  @Autowired
  private PolicyTemplateRepository policyTemplateRepository;

  private static final Logger LOGGER = LogManager.getLogger();

  @Override
  public ByteArrayOutputStream getBundle() {
    TarArchiveOutputStream taos = null;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    List<PolicyTemplateEntity> entities = policyTemplateRepository.findAll();

    try {

      taos = new TarArchiveOutputStream(new GZIPOutputStream(new BufferedOutputStream(baos)));

      for (PolicyTemplateEntity entity : entities) {
        String templateName = entity.getKey() + ".rego";
        LOGGER.info("Adding template: " + templateName + "...");
        TarArchiveEntry tae = new TarArchiveEntry(templateName);

        byte[] templateBytes = Base64.getDecoder().decode(entity.getRego());
//      byte[] templateBytes = entity.getRego().getBytes("UTF-8");
        LOGGER.info(new String(templateBytes, "StandardCharsets.UTF_8"));
        tae.setSize(templateBytes.length);
        taos.putArchiveEntry(tae);
        // The write command allows you to write bytes to the current entry
        // on the output stream. It will not allow you to write any more than the size
        // that you specified when you created the archive entry above
        taos.write(templateBytes);
        taos.closeArchiveEntry();
      }
    } catch (Exception e) {
      LOGGER.log(Level.ERROR, e);
      throw new BosunException(e.getMessage());
    } finally {
      try {
        if (taos != null) {
          taos.close();
        }

      } catch (IOException e) {
        LOGGER.log(Level.ERROR, e);
      }
    }

    return baos;
  }
}
