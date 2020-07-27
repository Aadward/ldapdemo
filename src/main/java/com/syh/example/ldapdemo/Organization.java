package com.syh.example.ldapdemo;

import javax.naming.Name;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Organization {

	@EqualsAndHashCode.Include
	private Name dn;

	private String name;

	public Organization(Name dn, String name) {
		this.dn = dn;
		this.name = name;
	}

	private Organization parent;
}
