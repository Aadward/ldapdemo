package com.syh.example.ldapdemo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Organization {

	private String dn;

	private String rdn;

	private String base;
}
