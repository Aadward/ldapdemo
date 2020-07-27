package com.syh.example.ldapdemo;

import javax.naming.Name;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class User {

	@EqualsAndHashCode.Include
	protected Name dn;

	protected String uid;

	protected String userName;
}
