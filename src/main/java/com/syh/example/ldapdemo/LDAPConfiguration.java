package com.syh.example.ldapdemo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LDAPConfiguration {

	private String uri;

	private String searchUserDn;

	private String searchUserPwd;

	private String userBaseDn;

	private String userClassName;

	private String userIdAttr;

	private String groupBaseDn;

	private String groupClassName;

	private String groupNameAttr;

	private String groupMembershipAttr;
}
