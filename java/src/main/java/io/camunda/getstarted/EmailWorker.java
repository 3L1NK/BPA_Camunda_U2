package io.camunda.getstarted;

import io.camunda.zeebe.client.ZeebeClient;
import java.util.Scanner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Map;
import java.util.HashMap;

public class EmailWorker {
  private static final Logger LOG = LogManager.getLogger(EmailWorker.class);

  public static void main(String[] args) {
    try (ZeebeClient client = ZeebeClientFactory.getZeebeClient()) {
      // Worker for "check_email" job type
      client.newWorker().jobType("check_email").handler((jobClient, job) -> {
        final String emailcontent;
        if (job.getVariablesAsMap().containsKey("req_email")) {
          emailcontent = (String) job.getVariablesAsMap().get("req_email");
        } else {
          emailcontent = (String) job.getVariablesAsMap().get("out_req_email");
        }

        LOG.info("Checking email content: {}", emailcontent);

        // Perform your email validation logic here
        boolean isEmailValid = isValidEmail(emailcontent);

        // Set the result in the variable
        Map<String, Object> vars = new HashMap<>();
        vars.put("email_valid", isEmailValid);

        // Complete the job with the result
        jobClient.newCompleteCommand(job.getKey()).variables(vars).send()
                .whenComplete((result, exception) -> {
                  if (exception == null) {
                    LOG.info("Completed job successfully with result: " + result);
                  } else {
                    LOG.error("Failed to complete job", exception);
                  }
                });
      }).open();

      // Worker for "verify_payment" job type
      client.newWorker().jobType("verify_payment").handler((jobClient, job) -> {
        final String paymentCard = (String) job.getVariablesAsMap().get("payment_card");

        LOG.info("Verifying payment card: {}", paymentCard);

        boolean isPaymentValid = isValidPaymentCard(paymentCard);

        Map<String, Object> vars = new HashMap<>();
        vars.put("payment_verified", isPaymentValid);

        jobClient.newCompleteCommand(job.getKey()).variables(vars).send()
                .whenComplete((result, exception) -> {
                  if (exception == null) {
                    LOG.info("Completed verify_payment job successfully with result: " + result);
                  } else {
                    LOG.error("Failed to complete verify_payment job", exception);
                  }
                });
      }).open();

      // Run until System.in receives the exit command
      waitUntilSystemInput("exit");
    }
  }
  private static boolean isValidEmail(String email) {
    // Perform your email validation logic here
    // For simplicity, let's check if the email contains "@mail.de"
    return email != null && email.contains("@mail.de");
  }
  private static boolean isValidPaymentCard(String paymentCard) {
    // Perform payment card validation logic here
    // For simplicity, let's assume a valid card if the length is 4
    return paymentCard != null && paymentCard.length() >= 4;
  }

  private static void waitUntilSystemInput(final String exitCode) {
    try (final Scanner scanner = new Scanner(System.in)) {
      while (scanner.hasNextLine()) {
        final String nextLine = scanner.nextLine();
        if (nextLine.contains(exitCode)) {
          return;
        }
      }
    }
  }
}
