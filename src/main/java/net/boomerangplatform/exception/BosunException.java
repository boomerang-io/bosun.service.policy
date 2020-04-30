package net.boomerangplatform.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BosunException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public BosunException(String message) {
    super(message);
  }
  
  public BosunException(Throwable e) {
    super(e);
  }
  
  public BosunException(String message, Throwable e) {
    super(message, e);
  }

}
