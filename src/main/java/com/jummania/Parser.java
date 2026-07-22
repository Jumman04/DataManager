package com.jummania;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public final class Parser {

    private final Serializer serializer = new Serializer();
    private final Deserializer deserializer = new Deserializer();

    void main() {

        Company company = createCompany();

        //  for (int i = 0; i < 3; i++) {
        parse(company);
        //  }

    }

    void parse(Company company) {
        byte[] binary = serialize(company);

        Gson gson = new Gson();

        String s = gson.toJson(company);


        gson.fromJson(s, Company.class);


        long start = System.nanoTime();

        for (int i = 0; i < 100000; i++) {
            gson.fromJson(s, Company.class);
        }

        long end = System.nanoTime();

        System.out.println((end - start) / 100000.0);
    }

    public byte[] serialize(Object obj) {
        ByteBuilder sb = new ByteBuilder();
        serializer.serialize(obj, sb);
        return sb.toByteArray();
    }

    public Company createCompany() {

        Company company = new Company();

        company.id = 1;
        company.name = "Jummania Ltd";

        company.headOffice = new Address();
        company.headOffice.country = "Bangladesh";
        company.headOffice.city = "Dhaka";
        company.headOffice.street = "Motijheel";
        company.headOffice.zipCode = 1000;

        company.departments = new Department[5];

        for (int i = 0; i < 5; i++) {

            Department d = new Department();

            d.id = i;
            d.name = "Department " + i;
            d.active = true;

            company.departments[i] = d;
        }

        company.employees = new ArrayList<>();

        for (int i = 0; i < 100; i++) {

            Employee e = new Employee();

            e.id = i;
            e.name = "Employee " + i;
            e.age = 20 + (i % 30);
            e.salary = 25000 + i * 1000;

            e.address = new Address();
            e.address.country = "Bangladesh";
            e.address.city = "Dhaka";
            e.address.street = "Road " + i;
            e.address.zipCode = 1000 + i;

            e.phones = new ArrayList<>();

            for (int j = 0; j < 3; j++) {

                Phone p = new Phone();

                p.type = "Mobile";
                p.number = "01700000" + i + j;

                e.phones.add(p);
            }

            e.skills = new Skill[5];

            for (int j = 0; j < 5; j++) {

                Skill s = new Skill();

                s.name = "Skill-" + j;
                s.level = (j % 10) + 1;

                e.skills[j] = s;
            }

            company.employees.add(e);
        }

        return company;
    }

    public class Company {

        public long id;
        public String name;

        public Address headOffice;

        public Department[] departments;

        public List<Employee> employees;
    }

    public class Address {

        public String country;
        public String city;
        public String street;
        public int zipCode;
    }

    public class Department {

        public int id;
        public String name;
        public boolean active;
    }

    public class Employee {

        public long id;
        public String name;
        public int age;
        public double salary;

        public Address address;

        public List<Phone> phones;

        public Skill[] skills;
    }

    public class Phone {

        public String type;
        public String number;
    }

    public class Skill {

        public String name;
        public int level;
    }
}
