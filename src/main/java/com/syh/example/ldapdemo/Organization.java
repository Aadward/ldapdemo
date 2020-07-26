package com.syh.example.ldapdemo;

import java.util.List;

import javax.naming.Name;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Organization {

	private Name dn;

	private String name;

	public Organization(Name dn, String name) {
		this.dn = dn;
		this.name = name;
	}

	private List<Organization> sub;
}
