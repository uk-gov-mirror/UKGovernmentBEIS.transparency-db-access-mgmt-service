package com.beis.subsidy.control.accessmanagementservice.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;
import uk.gov.service.notify.NotificationClient;
import uk.gov.service.notify.NotificationClientException;
import uk.gov.service.notify.SendEmailResponse;

@Slf4j
public class EmailUtils {

	public static void sendEmail(String emailId, String status,Long awardNumber, String userName,Environment environment) throws NotificationClientException {

		String templateId = "";
		String apiKey ="beis_notification-acabb994-cf6a-4d65-8632-1cc3ece74aa5-ef624de5-91dd-4f1b-8279-d970ee3949d5";
		NotificationClient client = new NotificationClient(apiKey);
		Map<String, Object> personalisation = new HashMap<>();
		personalisation.put("award_number", awardNumber);
		personalisation.put("approver_name", userName);
		if (status.equals("Published")) {
			templateId =  "5dd4c65e-4cd7-4954-9820-d958ac4fc0d9"; //environment.getProperty("award_approved_template");
		} else if(status.equals("Rejected")) {
			templateId = environment.getProperty("award_reject_template");
		}
		SendEmailResponse response = client.sendEmail(templateId, emailId, personalisation, null);

		log.info("response :: " + response.getBody());
	}



public static void sendFeedBack(String feedBack,String comments,String apiKey,String template, Environment environment) throws NotificationClientException {
	
		log.info("inside  sendFeedBack ***** email * :: ");
		NotificationClient client = new NotificationClient(apiKey);
		String feedBackEmail=environment.getProperty("feedBackEmail");
		log.info("before sending ***** email **8 :: {}", feedBackEmail);
		if(StringUtils.isEmpty(comments)) {
			comments="N/A";
		}
		Map<String, Object> personalisation = new HashMap<>();
		personalisation.put("feedback_comment", feedBack);
		personalisation.put("comments", comments);
		log.info("before sending ***** email ** :: ");
		SendEmailResponse response = client.sendEmail(template, feedBackEmail, personalisation, null);

		log.info("email sent ::{}");
	}
	
}
