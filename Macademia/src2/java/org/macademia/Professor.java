package org.macademia;

import java.util.HashSet;
import java.util.Set;

public class Professor {
	private String name;
	private String department;
	private String email;
	private Set<String> interests = new HashSet<String>();
	
	public Professor(String name) {
		this.name = name;
	}

	public Professor(String name, String department, String email) {
		this.name = name;
		this.email = email;
		this.department = department;
	}

	public String getEmail() {
		return email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public Set<String> getInterests() {
		return interests;
	}

	public void addInterest(String interest) {
		this.interests.add(interest);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Professor other = (Professor) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	public String toString() {
		return name + " (" + department + ")";
	}
	
}
