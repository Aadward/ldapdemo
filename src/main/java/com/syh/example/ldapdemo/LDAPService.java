package com.syh.example.ldapdemo;

import static org.springframework.ldap.query.LdapQueryBuilder.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.naming.Name;
import javax.naming.NamingException;

import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.stereotype.Component;

import javafx.util.Pair;

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

	public Organizations findOrganizations(LDAPConfiguration conf) {
		LdapTemplate template = LDAPTemplateFactory.create(conf);
		return new Organizations(findAllOrganizationsAsList(conf, template));
	}

	private List<Organization> findAllOrganizationsAsList(LDAPConfiguration conf, LdapTemplate template) {
		return template.search(
			query()
				.base(conf.getGroupBaseDn())
				.where("objectclass").is(conf.getGroupClassName()),
			new ContextMapper<Organization>() {
				@Override
				public Organization mapFromContext(Object ctx) throws NamingException {
					DirContextAdapter adapter = (DirContextAdapter)ctx;
					return new Organization(adapter.getDn(), adapter.getStringAttribute(conf.getGroupNameAttr()));
				}
			});
	}

	private List<User> findAllUsersAsList(LDAPConfiguration conf, LdapTemplate template) {
		return template.search(
			query()
				.base(conf.getUserBaseDn())
				.where("objectclass").is(conf.getUserClassName()),
			new ContextMapper<User>() {
				@Override
				public User mapFromContext(Object ctx) throws NamingException {
					DirContextAdapter adapter = (DirContextAdapter)ctx;
					return new User(adapter.getDn(), adapter.getStringAttribute(conf.getUserIdAttr()),
						adapter.getStringAttribute(conf.getUserNameAttr()));
				}
			}
		);
	}

	public Map<Organization, List<User>> findUsersGroupByOrganization(LDAPConfiguration conf) {
		LdapTemplate template = LDAPTemplateFactory.create(conf);

		Map<Name, Organization> organizationMap =
			findAllOrganizationsAsList(conf, template)
				.stream()
				.collect(Collectors.toMap(Organization::getDn, org -> org));

		Map<Name, User> userMap =
			findAllUsersAsList(conf, template)
				.stream()
				.collect(Collectors.toMap(User::getDn, user -> user));

		return template.search(
			query()
				.base(conf.getGroupBaseDn())
				.where("objectclass").is("groupOfUniqueNames"),
			new ContextMapper<Pair<Organization, List<User>>>() {
				@Override
				public Pair<Organization, List<User>> mapFromContext(Object ctx) throws NamingException {
					DirContextAdapter adapter = (DirContextAdapter)ctx;
					Name group = adapter.getDn().getPrefix(adapter.getDn().size() - 1);
					List<User> users = Arrays.stream(adapter.getStringAttributes("uniqueMember"))
						.map(userDn -> userMap.get(LdapUtils.newLdapName(userDn)))
						.collect(Collectors.toList());

					return new Pair<>(organizationMap.get(group), users);
				}
			})
			.stream()
			.collect(Collectors.toMap(Pair::getKey, Pair::getValue,
				(users1, users2) -> Stream.concat(users1.stream(), users2.stream())
					.distinct()
					.collect(Collectors.toList())));

	}
}
