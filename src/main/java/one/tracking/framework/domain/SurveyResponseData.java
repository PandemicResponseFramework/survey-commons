/**
 *
 */
package one.tracking.framework.domain;

import java.math.BigInteger;
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
  private BigInteger order;
  private String questionType;
  private String question;
  private String checkListEntry;
  private Boolean boolAnswer;
  private Integer numberAnswer;
  private String textAnswer;
  private String predefinedAnswer;
  private Integer version;
  private Boolean skipped;
  private Boolean valid;
  private Date createdAt;
}
