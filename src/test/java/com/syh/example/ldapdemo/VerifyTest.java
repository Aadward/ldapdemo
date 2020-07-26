package com.syh.example.ldapdemo;

import static org.springframework.ldap.query.LdapQueryBuilder.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.ldap.DataLdapTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.ldap.AuthenticationException;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.test.context.junit4.SpringRunner;

import com.syh.example.ldapdemo.Organization;
import com.syh.example.ldapdemo.User;

@RunWith(SpringRunner.class)
@DataLdapTest
public class VerifyTest {

	@Autowired
	private LdapTemplate ldapTemplate;

	@Test
	public void login_ok() {
		String uid = "bob";
		String password = "bobspassword";
		ldapTemplate.authenticate(
			query()
				.base("dc=springframework,dc=org")
				.where("objectclass").is("inetOrgPerson")
				.and("uid").is(uid),
			password);
	}

	@Test(expected = EmptyResultDataAccessException.class)
	public void login_baseNotCorrect() {
		String uid = "bob";
		String password = "bobspassword";
		ldapTemplate.authenticate(
			query()
				.base("ou=otherpeople,dc=springframework,dc=org")
				.where("objectclass").is("inetOrgPerson")
				.and("uid").is(uid),
			password);
	}

	@Test(expected = AuthenticationException.class)
	public void login_badPassword() {
		String uid = "bob";
		String password = "badPassword";
		ldapTemplate.authenticate(
			query()
				.base("dc=springframework,dc=org")
				.where("objectclass").is("inetOrgPerson")
				.and("uid").is(uid),
			password);
	}

	@Test
	public void findAllUser_ok() {
		List<User> list = ldapTemplate.search(
			query()
				.base("dc=springframework,dc=org")
				.where("objectclass").is("inetOrgPerson"),
			new AttributesMapper<Map<String, Object>>() {
				@Override
				public Map<String, Object> mapFromAttributes(Attributes attributes) throws NamingException {
					Map<String, Object> ret = new HashMap<>();
					NamingEnumeration<? extends Attribute> attris = attributes.getAll();
					while (attris.hasMore()) {
						Attribute abbr = attris.next();
						ret.put(abbr.getID(), abbr.get());
					}
					return ret;
				}
			})
			.stream()
			.map(map -> new User(map.get("uid").toString(), map.get("cn").toString()))
			.collect(Collectors.toList());

		list.forEach(user -> System.out.println(user + "\n"));
	}

	@Test
	public void findAllOrganization_ok() {
		List<Organization> organizations = ldapTemplate.search(
			query()
				.base("dc=springframework,dc=org")
				.where("objectclass").is("organizationalUnit"),
			new ContextMapper<Map<String, Object>>() {
				@Override
				public Map<String, Object> mapFromContext(Object ctx) throws NamingException {
					DirContextAdapter adapter = (DirContextAdapter)ctx;
					Map<String, Object> ret = new HashMap<>();
					ret.put("dn", adapter.getDn());
					ret.put("base", adapter.getNameInNamespace());

					Attributes attributes = adapter.getAttributes();
					NamingEnumeration<? extends Attribute> attris = attributes.getAll();
					while (attris.hasMore()) {
						Attribute abbr = attris.next();
						ret.put(abbr.getID(), abbr.get());
					}
					return ret;
				}
			})
			.stream()
			.map(map -> new Organization(LdapUtils.newLdapName(map.get("dn").toString()), map.get("base").toString()
				, null))
			.collect(Collectors.toList());

		organizations.forEach(or -> System.out.println(or + "\n"));
	}

	@Test
	public void test() {
		Name name = LdapUtils.newLdapName("ou=group,dc=example,dc=org");
		for (int i = 0; i <= name.size(); i++) {
			System.out.println(i + ":" + name.getPrefix(i));
		}
	}
}
