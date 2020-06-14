/**
 *
 */
package one.tracking.framework.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

/**
 * @author Marko Voß
 *
 */
@Configuration
@ConditionalOnProperty(name = "app.logging.request.enable", havingValue = "true")
public class RequestLoggingConfig {

  @Autowired
  private RequestLoggingProperties properties;

  @Bean
  public CommonsRequestLoggingFilter logFilter() {
    final CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
    filter.setIncludeQueryString(this.properties.isQueryString());
    filter.setIncludeClientInfo(this.properties.isClientInfo());
    filter.setIncludeHeaders(this.properties.isHeaders());
    filter.setIncludePayload(this.properties.isPayload());
    filter.setMaxPayloadLength(this.properties.getPayloadLength());
    return filter;
  }
}
