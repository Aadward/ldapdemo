package com.syh.example.ldapdemo;

import static org.springframework.ldap.query.LdapQueryBuilder.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.naming.Name;
import javax.naming.NamingException;

import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

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

	//TODO:
	public List<Organization> findOrganizations(LDAPConfiguration conf) {
		LdapTemplate template = LDAPTemplateFactory.create(conf);
		Map<Name, Organization> organizations = template.search(
			query()
				.base(conf.getGroupBaseDn())
				.where("objectclass").is(conf.getGroupClassName()),
			new ContextMapper<Organization>() {
				@Override
				public Organization mapFromContext(Object ctx) throws NamingException {
					DirContextAdapter adapter = (DirContextAdapter)ctx;
					return new Organization(adapter.getDn(), adapter.getStringAttributes(conf.getGroupNameAttr())[0]);
				}
			})
			.stream()
			.collect(Collectors.toMap(Organization::getDn, org -> org));

		return Lists.newArrayList(organizations.values());
	}
}
