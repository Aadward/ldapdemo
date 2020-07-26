package com.syh.example.ldapdemo;

import static org.springframework.ldap.query.LdapQueryBuilder.*;

import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Component;

@Component
public class LDAPService {

	public void authenticate(LDAPConfiguration conf, String uid, String pwd) {
		LdapTemplate template = LDAPTemplateFactory.create(conf);
		template.authenticate(
			query()
				.base(conf.getUserBaseDn())
				.where("objectclass").is(conf.getUserClassName())
				.and(conf.getUserIdAttr()).is(uid),
			pwd);
	}
}
