package model.services;

import java.util.ArrayList;
import java.util.List;

import model.entites.Department;

public class DepartmentServices {

	public List<Department> findAll(){
		List<Department> list = new ArrayList<>();
		list.add(new Department(1, "Books"));
		list.add(new Department(2, "Computers"));
		list.add(new Department(3, "Electronics"));
		return list;
	}
}
