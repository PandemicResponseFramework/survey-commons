/**
 *
 */
package one.tracking.framework.domain;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @author Marko Voß
 *
 */
@Data
@Builder
@AllArgsConstructor
public class SurveyResponseData {

  private String nameId;
  private Date startTime;
  private Date endTime;
  private String userId;
  private Number order;
  private String questionType;
  private String question;
  private String checkListEntry;
  private Boolean boolAnswer;
  private Number numberAnswer;
  private String textAnswer;
  private String predefinedAnswer;
  private Number version;
  private Boolean skipped;
  private Boolean valid;
  private Date createdAt;
}
