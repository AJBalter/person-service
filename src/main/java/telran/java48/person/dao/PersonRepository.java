package telran.java48.person.dao;

import java.time.LocalDate;
import java.util.stream.Stream;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import telran.java48.person.dto.CityPopulationDto;
import telran.java48.person.model.Person;

public interface PersonRepository extends CrudRepository<Person, Integer> {
	Stream<Person> findByAddressCityIgnoreCase(String city);

	Stream<Person> findByNameIgnoreCase(String name);

	Stream<Person> findByBirthDateBetween(LocalDate startDate, LocalDate endDate);

	@Query("select new telran.java48.person.dto.CityPopulationDto(p.address.city, count(p)) from Person p group by p.address.city order by count(p) asc")
	Stream<CityPopulationDto> getCitiesPopulation();

	@Query("select new telran.java48.person.model.Child(p.id, p.name, p.birthDate, p.address, c.kindergarten) from Person p join Child c ON p.id = c.id")
	Stream<Person> findAllChildren();


	@Query("select new telran.java48.person.model.Employee(p.id, p.name, p.birthDate, p.address, e.company, e.salary) " +
		   "from Person p join Employee e on p.id = e.id " +
		   "where p.id IN (select e.id from Employee e) " +
		   "and e.salary between :minSalary and :maxSalary " + 
		   "order by e.salary asc")
	Stream<Person> findBySalaryBetween(Integer minSalary, Integer maxSalary);
}
