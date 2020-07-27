package com.syh.example.ldapdemo;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ldap.embedded.EmbeddedLdapProperties;
import org.springframework.boot.test.autoconfigure.data.ldap.DataLdapTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.ldap.AuthenticationException;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataLdapTest
@ComponentScan(basePackages = "com.syh.example.ldapdemo")
public class LDAPServiceTest {

	@Autowired
	private LDAPService ldapService;

	@Autowired
	private EmbeddedLdapProperties properties;

	private LDAPConfiguration conf;

	@Before
	public void init() {
		conf = LDAPConfiguration.builder()
			.uri("ldap://localhost:" + properties.getPort() + "/")
			.userBaseDn("dc=springframework,dc=org")
			.userClassName("inetOrgPerson")
			.userIdAttr("uid")
			.userNameAttr("cn")
			.groupBaseDn("ou=groups,dc=springframework,dc=org")
			.groupClassName("organizationalUnit")
			.groupNameAttr("ou")
			.build();
	}

	@Test
	public void authenticate() throws Exception {
		ldapService.authenticate(conf, "bob", "bobspassword");
	}

	@Test(expected = AuthenticationException.class)
	public void authenticate_badPassword() throws Exception {
		ldapService.authenticate(conf, "bob", "badPassword");
	}

	@Test(expected = EmptyResultDataAccessException.class)
	public void authenticate_canNotFoundUser() throws Exception {
		ldapService.authenticate(conf, "shenyuhang", "pwd");
	}

	@Test
	public void findOrganizations() throws Exception {
		Organizations organizations = ldapService.findOrganizations(conf);
		assertThat(organizations.size()).isEqualTo(2);

		// check tree
		System.out.println(organizations.toString());
	}

	@Test
	public void findUsersByOrganizations() throws Exception {
		Organizations organizations = ldapService.findOrganizations(conf);

		Map<Organization, List<User>> users = ldapService.findUsersGroupByOrganization(conf);
		assertThat(users).size().isEqualTo(2);

		// check users

		System.out.println("-----------------------------------");
		System.out.println("Users Grouped By organization: \n");
		System.out.println("-----------------------------------");
		users.forEach((org, us) -> {
			String usNames = String.join(",", us.stream().map(User::getUserName).collect(Collectors.toList()));
			System.out.println(org.getName() + " : " + usNames + "\n");
		});
	}

}
