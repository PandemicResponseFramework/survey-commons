/**
 *
 */
package one.tracking.framework.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.annotation.Validated;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Marko Voß
 *
 */
@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@ConfigurationProperties(prefix = "app.logging.request.include")
@ConstructorBinding
@Validated
public class RequestLoggingProperties {

  private boolean queryString = true;

  private boolean clientInfo = false;

  private boolean headers = false;

  private boolean payload = false;

  private int payloadLength = 1000;

}
