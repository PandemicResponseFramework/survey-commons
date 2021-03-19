/**
 *
 */
package one.tracking.framework.support;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import one.tracking.framework.domain.Period;
import one.tracking.framework.domain.SurveyStatusType;
import one.tracking.framework.entity.SurveyResponse;
import one.tracking.framework.entity.meta.Answer;
import one.tracking.framework.entity.meta.Survey;
import one.tracking.framework.entity.meta.container.Container;
import one.tracking.framework.entity.meta.question.BooleanQuestion;
import one.tracking.framework.entity.meta.question.ChecklistEntry;
import one.tracking.framework.entity.meta.question.ChecklistQuestion;
import one.tracking.framework.entity.meta.question.ChoiceQuestion;
import one.tracking.framework.entity.meta.question.Question;

/**
 * @author Marko Voß
 *
 */
@Component
public final class ServiceUtility {

  private static final Random RANDOM = new Random();

  public String generateValidToken(final int length, final int retries, final Predicate<String> existsPredicate)
      throws IllegalStateException {

    Assert.isTrue(length > 0, "Length must be greater than zero.");
    Assert.isTrue(retries > 0, "Retries must be greater than zero.");
    Assert.notNull(existsPredicate, "Predicate must not be null.");

    for (int i = 0; i < retries; i++) {
      final String token = generateString(length);
      if (!existsPredicate.test(token))
        return token;
    }
    throw new IllegalStateException("Unable to generate valid token.");
  }

  /**
   *
   * @param length
   * @return
   */
  public String generateString(final int length) {
    final int leftLimit = 48; // numeral '0'
    final int rightLimit = 122; // letter 'z'

    final String generatedString = RANDOM.ints(leftLimit, rightLimit + 1)
        .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
        .limit(length)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();

    return generatedString;
  }

  public Period getCurrentSurveyInstancePeriod(final Survey survey) {

    Assert.notNull(survey, "Survey must not be null.");

    switch (survey.getIntervalType()) {
      case NONE:
        return Period.INFINITE;
      default:
        return getCurrentPeriod(survey);
    }
  }

  public Period getNextSurveyInstancePeriod(final Survey survey, final ZonedDateTime notBefore) {

    Assert.notNull(survey, "Survey must not be null.");

    switch (survey.getIntervalType()) {
      case NONE:
        return Period.INFINITE;
      default:
        return getNextPeriod(survey, notBefore);
    }
  }

  /**
   * @param survey
   * @return
   */
  private Period getCurrentPeriod(final Survey survey) {

    final Period nextPeriod = getNextPeriod(survey, ZonedDateTime.now(ZoneOffset.UTC));
    return new Period(
        ZonedDateTime.ofInstant(nextPeriod.getStart(), ZoneOffset.UTC)
            .minus(survey.getIntervalValue(), survey.getIntervalType().toChronoUnit()).toInstant(),
        ZonedDateTime.ofInstant(nextPeriod.getEnd(), ZoneOffset.UTC)
            .minus(survey.getIntervalValue(), survey.getIntervalType().toChronoUnit()).toInstant());
  }

  /**
   *
   * @param survey
   * @param notBefore
   * @return
   */
  private Period getNextPeriod(final Survey survey, final ZonedDateTime notBefore) {

    final ZonedDateTime start = survey.getIntervalStart().atZone(ZoneOffset.UTC);

    if (start.isAfter(notBefore)) {
      return new Period(
          start.toInstant(),
          start.plus(survey.getIntervalValue(), survey.getIntervalType().toChronoUnit()).minusSeconds(1).toInstant());
    }

    final long intervalLength = survey.getIntervalType().toChronoUnit().getDuration().toSeconds();
    final long secondsDelta = (notBefore.toEpochSecond() - start.toEpochSecond());
    final long diff = intervalLength - secondsDelta % intervalLength;

    final ZonedDateTime startTime = notBefore.plusSeconds(diff)
        .withMinute(start.getMinute())
        .withSecond(start.getSecond())
        .withNano(start.getNano());

    final ZonedDateTime endTime = startTime.plus(survey.getIntervalValue(), survey.getIntervalType().toChronoUnit())
        .minusSeconds(1);

    return new Period(startTime.toInstant(), endTime.toInstant());
  }

  // // FIXME DELETE
  // public static void main(final String[] args) {
  // final Survey survey = Survey.builder()
  // .intervalType(IntervalType.WEEKLY)
  // .intervalStart(Instant.parse("2020-10-18T10:00:00Z"))
  // .intervalValue(1)
  // .build();
  //
  // final ServiceUtility self = new ServiceUtility();
  // System.out.println(self.getNextPeriod(survey, ZonedDateTime.now(ZoneOffset.UTC)));
  // }

  public SurveyStatusType calculateSurveyStatus(final Survey survey, final List<SurveyResponse> surveyResponses) {

    final Map<Long, SurveyResponse> responses =
        surveyResponses.stream().collect(Collectors.toMap(e -> e.getQuestion().getId(), e -> e));

    if (responses.isEmpty())
      return SurveyStatusType.INCOMPLETE;

    if (checkAnswers(survey.getQuestions(), responses))
      return SurveyStatusType.COMPLETED;

    return SurveyStatusType.INCOMPLETE;
  }

  private boolean checkAnswers(final List<Question> questions, final Map<Long, SurveyResponse> responses) {

    if (questions == null || responses == null || responses.isEmpty())
      return false;

    for (final Question question : questions) {

      if (!isAnswered(question, responses))
        return false;

      if (isSubQuestionRequired(question, responses.get(question.getId()))) {
        if (!checkAnswers(getQuestions(question), responses))
          return false;
      }
    }

    return true;
  }

  private List<Question> getQuestions(final Question question) {

    switch (question.getType()) {
      case BOOL:
        return ((BooleanQuestion) question).getContainer().getQuestions();
      case CHOICE:
        return ((ChoiceQuestion) question).getContainer().getQuestions();
      default:
        return null;

    }
  }

  /**
   *
   * @param question
   * @param response
   * @return
   */
  private boolean isSubQuestionRequired(final Question question, final SurveyResponse response) {

    if (question == null || !question.hasContainer() || response == null || !response.isValid()
        || (question.isOptional() && response.isSkipped()))
      return false;

    switch (question.getType()) {
      case BOOL:

        final BooleanQuestion booleanQuestion = (BooleanQuestion) question;
        if (booleanQuestion.getContainer().getDependsOn() == null)
          return true;

        return response.getBoolAnswer().equals(booleanQuestion.getContainer().getDependsOn());

      case CHOICE:

        final ChoiceQuestion choiceQuestion = (ChoiceQuestion) question;
        if (choiceQuestion.getContainer().getDependsOn() == null)
          return true;

        final List<Long> givenAnswerIds =
            response.getAnswers().stream().map(Answer::getId).collect(Collectors.toList());

        // anyMatch -> OR-linked answers
        // allMatch -> AND-linked answers
        return choiceQuestion.getContainer().getDependsOn().stream().anyMatch(p -> givenAnswerIds.contains(p.getId()));

      default:
        return true;

    }
  }

  private boolean isAnswered(final Question question, final Map<Long, SurveyResponse> responses) {

    switch (question.getType()) {
      case BOOL: {

        final SurveyResponse response = responses.get(question.getId());
        return response != null && response.isValid()
            && (response.getBoolAnswer() != null || question.isOptional() && response.isSkipped());

      }
      case CHECKLIST: {

        final ChecklistQuestion checklistQuestion = (ChecklistQuestion) question;
        for (final ChecklistEntry entry : checklistQuestion.getEntries()) {

          final SurveyResponse response = responses.get(entry.getId());
          if (response == null || !response.isValid()
              || (!question.isOptional() && response.getBoolAnswer() == null)
              || (question.isOptional() && response.getBoolAnswer() == null && !response.isSkipped()))
            return false;
        }

        return true;

      }
      case CHOICE: {

        final ChoiceQuestion choiceQuestion = (ChoiceQuestion) question;
        final SurveyResponse response = responses.get(question.getId());

        if (response == null || !response.isValid()
            || (!question.isOptional() && (response.getAnswers() == null || response.getAnswers().isEmpty()))
            || (question.isOptional() && (response.getAnswers() == null || response.getAnswers().isEmpty())
                && !response.isSkipped()))
          return false;

        if (question.isOptional() && response.isSkipped())
          return true;

        // Is the given answer part of the possible answers (data integrity validation)
        for (final Answer answer : choiceQuestion.getAnswers()) {
          if (response.getAnswers().stream().anyMatch(p -> p.getId().equals(answer.getId())))
            return true;
        }

        return false;

      }
      case NUMBER:
      case RANGE: {

        final SurveyResponse response = responses.get(question.getId());
        return response != null && response.isValid()
            && (response.getNumberAnswer() != null || question.isOptional() && response.isSkipped());

      }
      case TEXT: {

        final SurveyResponse response = responses.get(question.getId());
        return response != null && response.isValid()
            && ((response.getTextAnswer() != null && !response.getTextAnswer().isBlank())
                || question.isOptional() && response.isSkipped());

      }
      default:
        return false;
    }
  }

  public List<Question> traverseQuestions(final Container container, final Predicate<Question> filter,
      final Consumer<Question> consumer) {

    final List<Question> consumedQuestions = new ArrayList<>();

    for (final Question question : container.getQuestions()) {

      if (filter.test(question)) {
        consumer.accept(question);
        consumedQuestions.add(question);
      }

      if (question.hasContainer()) {
        consumedQuestions.addAll(traverseQuestions(question.getContainer(), filter, consumer));
      }
    }

    return consumedQuestions;
  }
}
