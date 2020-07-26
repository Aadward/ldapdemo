package com.syh.example.ldapdemo;

import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.DefaultDirObjectFactory;
import org.springframework.ldap.core.support.LdapContextSource;

import com.google.common.base.Strings;

public class LDAPTemplateFactory {

	public static LdapTemplate create(LDAPConfiguration conf) {
		LdapContextSource lcs = new LdapContextSource();
		lcs.setUrl(conf.getUri());
		if (!Strings.isNullOrEmpty(conf.getSearchUserDn()) && !Strings.isNullOrEmpty(conf.getSearchUserPwd())) {
			lcs.setUserDn(conf.getSearchUserDn());
			lcs.setPassword(conf.getSearchUserPwd());
		}
		lcs.setDirObjectFactory(DefaultDirObjectFactory.class);
		lcs.afterPropertiesSet();
		return new LdapTemplate(lcs);
	}
}
