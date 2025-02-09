package com.beis.subsidy.control.accessmanagementservice.utils;

import com.beis.subsidy.control.accessmanagementservice.exception.AccessManagementException;
import com.beis.subsidy.control.accessmanagementservice.exception.UnauthorisedAccessException;
import com.beis.subsidy.control.accessmanagementservice.model.AuditLogs;
import com.beis.subsidy.control.accessmanagementservice.repository.AuditLogsRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

/**
 * 
 * Search Utility class 
 */
@Slf4j
public class SearchUtils {

	/**
	 * To check if input string is null or empty
	 *
	 * @param inputString - input string
	 * @return boolean - true or false
	 */
	public static boolean checkNullOrEmptyString(String inputString) {
		return inputString == null || inputString.trim().isEmpty();
	}

	/**
	 * To convert string date in format YYYY-MM-DD to LocalDate (without timezone)
	 *
	 * @param inputStringDate - input string date
	 * @return
	 */
	public static LocalDate stringToDate(String inputStringDate) {
		return LocalDate.parse(inputStringDate);
	}

	/**
	 * To convert string date in format YYYY-MM-DD to DD FullMONTHNAME YYYY
	 *
	 * @param inputStringDate - input string date
	 * @return
	 */
	public static String dateToFullMonthNameInDate(LocalDate inputStringDate) {
		log.info("input Date ::{}", inputStringDate);
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd MMMM yyyy");
		return dateFormat.format(inputStringDate);
	}

	/**
	 * To convert BigDecimal to string by adding , for thousands.
	 *
	 * @param subsidyFullAmountExact
	 * @return
	 */
	public static String decimalNumberFormat(BigDecimal subsidyFullAmountExact) {
		DecimalFormat numberFormat = new DecimalFormat("###,###.##");
		return numberFormat.format(subsidyFullAmountExact.longValue());
	}

	/**
	 * To convert Amount Range to by adding pound and , for thousands.
	 *
	 * @param amountRange
	 * @return formatted string
	 */
	public static String formatedFullAmountRange(String amountRange) {
		String finalAmtRange = "NA";
		if (StringUtils.isNotBlank(amountRange) &&
				!(amountRange.equalsIgnoreCase("NA") || amountRange.contains("N/A")
						|| amountRange.contains("n/a"))
				&& !amountRange.endsWith(">")) {

			StringBuilder format = new StringBuilder();
			String[] tokens = amountRange.split("-");
			if (tokens.length == 2) {
				finalAmtRange = format.append(convertDecimalValue(tokens[0]))
						.append(" - ")
						.append("£")
						.append(decimalNumberFormat(new BigDecimal(tokens[1].trim()))).toString();
			} else {
				finalAmtRange = new BigDecimal(amountRange).longValue() > 0 ? format.append("£")
						.append(decimalNumberFormat(new BigDecimal(amountRange))).toString() : "0";
			}

		} else if (StringUtils.isNotBlank(amountRange) && amountRange.endsWith(">")) {
			String removedLessThanVal = amountRange.substring(0, amountRange.length() - 1).trim();
			finalAmtRange = "£" + decimalNumberFormat(new BigDecimal(removedLessThanVal)) + " or more";
		}
		return finalAmtRange;
	}

	public static String convertDecimalValue(String token) {
		String formatNumber = "";
		if (!token.contains("NA/na")) {
			String removedLessThanVal = token.contains(">") ? token.substring(1, token.length()).trim() : token.trim();
			formatNumber = decimalNumberFormat(new BigDecimal(removedLessThanVal));
			if (token.contains(">")) {
				formatNumber = ">£" + formatNumber;
			} else {
				formatNumber = "£" + formatNumber;
			}
		}
		return formatNumber;
	}

	public static String getDurationInYears(BigInteger noOfdays) {
		StringBuffer yearsStr = new StringBuffer();
		Integer days = noOfdays.intValue();
		int years = (days / 365);
		int months = ((days % 365) / 7) / 4;
		int weeks = ((days % 365) / 7) % 4;
		days = (days % 365) % 7;
		if (years > 0 && years == 1) {
			yearsStr.append(years + " year ");
		} else if (years > 0 && years > 1) {
			yearsStr.append(years + " years ");
		}
		if (months > 0 && months == 1) {
			yearsStr.append(months + " month ");
		} else if (months > 0 && months > 1) {
			yearsStr.append(months + " months ");
		}
		if (weeks > 0 && weeks == 1) {
			yearsStr.append(weeks + " week ");
		} else if (weeks > 0 && weeks > 1) {
			yearsStr.append(weeks + " weeks ");
		}
		if (days > 0 && days == 1) {
			yearsStr.append(days + " day ");
		} else if (days > 0 && days > 1) {
			yearsStr.append(days + " days ");
		}
		return yearsStr.toString();
	}

	public static UserPrinciple isRoleValid(ObjectMapper objectMapper, HttpHeaders userPrinciple) {
		UserPrinciple userPrincipleObj = null;
		String userPrincipleStr = userPrinciple.get("userPrinciple").get(0);
		try {
			userPrincipleObj = objectMapper.readValue(userPrincipleStr, UserPrinciple.class);
			if (!Arrays.asList(AccessManagementConstant.All_ROLES).contains(userPrincipleObj.getRole())) {
				throw new UnauthorisedAccessException("You are not authorised to access Admin Dashboard");
			}
		} catch (JsonProcessingException exception) {
			throw new AccessManagementException(HttpStatus.BAD_REQUEST, "JSON parsing Exception");
		}
		return userPrincipleObj;
	}

	public static UserPrinciple validateRoleFromUserPrincipleObject(ObjectMapper objectMapper, HttpHeaders userPrinciple,
																	String verifyRole) {
		UserPrinciple userPrincipleObj = null;

		try {
			String userPrincipleStr = userPrinciple.get("userPrinciple").get(0);

			userPrincipleObj = objectMapper.readValue(userPrincipleStr, UserPrinciple.class);
			if (!userPrincipleObj.getRole().equals(verifyRole)) {
				throw new UnauthorisedAccessException("You are not authorised to view Admin Dashboard");
			}
		} catch (JsonProcessingException exception) {
			throw new AccessManagementException(HttpStatus.BAD_REQUEST, "JSON parsing Exception");
		}
		return userPrincipleObj;
	}

	public static UserPrinciple validateAdminGAApproverRoleFromUpObj(ObjectMapper objMapper, HttpHeaders userPrinciple) {
		UserPrinciple userPrincipleObj = null;
		String userPrincipleStr = userPrinciple.get("userPrinciple").get(0);
		try {
			userPrincipleObj = objMapper.readValue(userPrincipleStr, UserPrinciple.class);
			if (!Arrays.asList(AccessManagementConstant.ROLES).contains(userPrincipleObj.getRole())) {
				throw new UnauthorisedAccessException("You are not authorised to access User Dashboard");
			}
		} catch (JsonProcessingException exception) {
			throw new AccessManagementException(HttpStatus.BAD_REQUEST, "JSON parsing Exception");
		}
		return userPrincipleObj;
	}

	public static UserPrinciple adminRoleValidFromUserPrincipleObject(ObjectMapper objectMapper, HttpHeaders userPrinciple) {
		UserPrinciple userPrincipleObj = null;
		String userPrincipleStr = userPrinciple.get("userPrinciple").get(0);
		try {
			userPrincipleObj = objectMapper.readValue(userPrincipleStr, UserPrinciple.class);
			if (!Arrays.asList(AccessManagementConstant.ADMIN_ROLES).contains(userPrincipleObj.getRole())) {
				throw new UnauthorisedAccessException("You are not authorised to Add or delete User");
			}
		} catch (JsonProcessingException exception) {
			throw new AccessManagementException(HttpStatus.BAD_REQUEST, "JSON parsing Exception");
		}
		return userPrincipleObj;
	}

	public static String getUserName(ObjectMapper objMapper, HttpHeaders userPrinciple) {
		UserPrinciple userPrincipleObj = null;
		String userPrincipleStr = userPrinciple.get("userPrinciple").get(0);
		try {
			userPrincipleObj = objMapper.readValue(userPrincipleStr, UserPrinciple.class);
			return userPrincipleObj.getUserName();

		} catch (JsonProcessingException exception) {
			throw new AccessManagementException(HttpStatus.BAD_REQUEST, "JSON parsing Exception");
		}

	}

	public static void saveAuditLog(UserPrinciple userPrinciple, String action,String msg,String userId,
								   AuditLogsRepository auditLogsRepository) {
		AuditLogs audit = new AuditLogs();
		try {
			String userName = userPrinciple.getUserName();
			audit.setUserName(userName);
			audit.setEventType(action);
			audit.setEventId(userId);
			audit.setEventMessage(msg);
			audit.setGaName(userPrinciple.getGrantingAuthorityGroupName());
			audit.setCreatedTimestamp(LocalDate.now());
			auditLogsRepository.save(audit);
		} catch(Exception e) {
			log.error("{} :: saveAuditLog failed to perform action", e);
		}

	}

	public static void saveAuditLogForUpdate(UserPrinciple userPrinciple, String action,String awardNo, String eventMsg,
											 AuditLogsRepository auditLogsRepository) {
		AuditLogs audit = new AuditLogs();
		try {
			String userName = userPrinciple.getUserName();
			audit.setUserName(userName);
			audit.setEventType(action);
			audit.setEventId(awardNo);
			audit.setEventMessage(eventMsg.toString());
			audit.setGaName(userPrinciple.getGrantingAuthorityGroupName());
			audit.setCreatedTimestamp(LocalDate.now());
			auditLogsRepository.save(audit);
		} catch(Exception e) {
			log.error("{} :: saveAuditLogForUpdate failed to perform action", e);
		}
	}

}
