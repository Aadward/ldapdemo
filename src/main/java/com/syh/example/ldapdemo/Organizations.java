package com.syh.example.ldapdemo;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

import javax.naming.Name;

import org.springframework.ldap.support.LdapUtils;

import com.google.common.collect.Lists;

/**
 *
 * @author shen.yuhang
 * created on 2020/7/27
 **/
public class Organizations {

	private static final Organization ROOT = new Organization(LdapUtils.newLdapName(""), "");

	private Map<Organization, List<Organization>> organizationTree;

	public Organizations(List<Organization> organizations) {
		this.init(organizations);
	}

	private void init(List<Organization> organizations) {
		Map<Name, Organization> allOrganizations = organizations
			.stream()
			.collect(Collectors.toMap(Organization::getDn, org -> org));

		allOrganizations.forEach((name, org) ->
			org.setParent(allOrganizations.get(name.getPrefix(name.size() - 1))));

		organizationTree = allOrganizations.values()
			.stream()
			.peek(org -> {
				if (org.getParent() == null) {
					org.setParent(ROOT);
				}
			})
			.collect(Collectors.groupingBy(Organization::getParent));

		assert organizationTree.get(ROOT).size() == 1;
	}

	public Organization findRoot() {
		return organizationTree.get(ROOT).get(0);
	}

	public List<Organization> findChild(Organization parent) {
		return organizationTree.getOrDefault(parent, Lists.newArrayList());
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("------------------------------------------").append("\n")
			.append("Organizations Tree")
			.append("\n")
			.append("------------------------------------------")
			.append("\n");

		Queue<Organization> queue = Lists.newLinkedList();
		queue.add(findRoot());
		while (!queue.isEmpty()) {
			Organization next = queue.poll();
			List<Organization> subs = findChild(next);
			sb.append("parent:").append(next.getName()).append("\n");
			for (Organization sub : subs) {
				sb.append(next.getName()).append("   ----->   ").append(sub.getName()).append("\n");
			}
			sb.append("\n").append("------------------------------------------").append("\n");
			queue.addAll(subs);
		}

		return sb.toString();
	}

	public long size() {
		return organizationTree.values()
			.stream()
			.mapToLong(List::size)
			.sum();
	}
}
