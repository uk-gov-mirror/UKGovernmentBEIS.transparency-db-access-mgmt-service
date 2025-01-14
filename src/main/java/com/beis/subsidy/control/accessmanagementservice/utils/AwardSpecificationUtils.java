package com.beis.subsidy.control.accessmanagementservice.utils;

import com.beis.subsidy.control.accessmanagementservice.model.AuditLogs;
import com.beis.subsidy.control.accessmanagementservice.model.Award;
import org.springframework.data.jpa.domain.Specification;

import java.text.MessageFormat;
import java.time.LocalDate;

public final class AwardSpecificationUtils {

	public static Specification<Award> awardByGrantingAuthority(Long gaId) {
		return (root, query, builder) -> builder.equal(root.get("grantingAuthority").get("gaId"), gaId);
	}

	/**
	 * To define specification for award status
	 * @param status
	 * @return Specification<Award>
	 */
	public static Specification<Award> awardByStatus(String status) {
		return (root, query, builder) -> builder.equal(root.get("status"), status);
	}

	/**
	 * To define specification for subsidy measure title
	 *
	 * @param subsidyMeasureTitle - Add subsidy measure title
	 * @return Specification<Award> - Specification for Award
	 */
	public static Specification<Award> subsidyMeasureTitle(String subsidyMeasureTitle) {

		return (root, query, builder) -> builder.like(builder.lower(root.get("subsidyMeasure").get("subsidyMeasureTitle")),
				builder.lower(builder.literal("%" + subsidyMeasureTitle.trim() + "%")));
	}

	/**
	 * To define specification for subsidy measure title
	 *
	 * @param subsidyNumber - Add subsidy measure title
	 * @return Specification<Award> - Specification for Award
	 */
	public static Specification<Award> subsidyNumber(String subsidyNumber) {
		return (root, query, builder) -> builder.equal(root.get("subsidyMeasure").get("scNumber"), subsidyNumber);
	}

	/**
	 * To check contains operations
	 * @param expression - input string
	 * @return - message format with like expression
	 */
	private static String contains(String expression) {
		return MessageFormat.format("%{0}%", expression);
	}


	public static Specification<Award> grantingAuthorityName(String searchName) {

		return (root, query, builder) -> builder.like(builder.lower(root.get("grantingAuthority").get("grantingAuthorityName")),
				builder.lower(builder.literal("%" + searchName.trim() + "%")));
	}

	public static Specification<Award> beneficiaryName(String beneficiaryName) {

		return (root, query, builder) -> builder.like(builder.lower(root.get("beneficiary").get("beneficiaryName")),
				builder.lower(builder.literal("%" + beneficiaryName.trim() + "%")));
	}

    public static Specification<Award> awardByNumber(Long awardNumber) {
		return (root, query, builder) -> builder.equal(root.get("awardNumber"),awardNumber);
    }
    
    /**
	 * To define specification for subsidy measure title
	 *
	 * @param userName - Add subsidy measure title
	 * @return Specification<Award> - Specification for Award
	 */
	public static Specification<AuditLogs> auditUserLikeSearch(String userName) {

		return (root, query, builder) -> builder.like(builder.lower(root.get("userName")),
				builder.lower(builder.literal("%" + userName.trim() + "%")));
	}

	public static Specification<AuditLogs> auditUserEqualSearch(String userName) {

		return (root, query, builder) -> builder.equal(root.get("userName"), userName);
	}
	/**
	 * To define specification for audit logs
	 *
	 * @param gaName - search by ga name
	 * @return Specification<AuditLogs> - Specification for AuditLogs
	 */
	public static Specification<AuditLogs> auditGrantingAuthority(String gaName) {
		
		return (root, query, builder) -> builder.like(builder.lower(root.get("gaName")),
				builder.lower(builder.literal("%" + gaName.trim() + "%")));
	}

	public static Specification<AuditLogs> searchByGrantingAuthorityName(String gaName) {

		return (root, query, builder) -> builder.equal(root.get("gaName"),
				gaName.trim());
	}
	
	public static Specification<AuditLogs> auditLogRange(LocalDate fromDate, LocalDate toDate) {

	    return (root, query, builder) -> builder.between(root.get("createdTimestamp"), fromDate, toDate);
	}
	
}
