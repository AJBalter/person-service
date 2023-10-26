package telran.java48.person.service;

import java.time.LocalDate;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import telran.java48.person.dao.PersonRepository;
import telran.java48.person.dto.AddressDto;
import telran.java48.person.dto.ChildDto;
import telran.java48.person.dto.CityPopulationDto;
import telran.java48.person.dto.EmployeeDto;
import telran.java48.person.dto.PersonDto;
import telran.java48.person.dto.exceptions.PersonNotFoundException;
import telran.java48.person.model.Address;
import telran.java48.person.model.Child;
import telran.java48.person.model.Employee;
import telran.java48.person.model.Person;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements PersonService, CommandLineRunner {

	final PersonRepository personRepository;
	final ModelMapper modelMapper;

	@Override
	@Transactional
	public Boolean addPerson(PersonDto personDto) {
		if (personRepository.existsById(personDto.getId())) {
			return false;
		}
		Person person = modelMapper.map(personDto, checkModelClass(personDto));
		if (person != null) {
			personRepository.save(person);
			return true;
		}

		return false;
	}

	private Class<? extends Person> checkModelClass(PersonDto personDto) {
		if (personDto instanceof ChildDto) {
			return Child.class;
		} else if (personDto instanceof EmployeeDto) {
			return Employee.class;
		} else if (personDto instanceof PersonDto) {
			return Person.class;
		}
		return null;
	}

	private Class<? extends PersonDto> checkDtoClass(Person person) {
		if (person instanceof Employee) {
			return EmployeeDto.class;
		} else if (person instanceof Child) {
			return ChildDto.class;
		} else if (person instanceof Person) {
			return PersonDto.class;
		}
		throw new IllegalArgumentException("Unknown Person type");
	}

	@Override
	public PersonDto findPersonById(Integer id) {
		Person person = personRepository.findById(id).orElseThrow(PersonNotFoundException::new);
		return modelMapper.map(person, checkDtoClass(person));
	}

	@Override
	@Transactional
	public PersonDto removePerson(Integer id) {
		Person person = personRepository.findById(id).orElseThrow(PersonNotFoundException::new);
		personRepository.delete(person);
		return modelMapper.map(person, checkDtoClass(person));
	}

	@Override
	@Transactional
	public PersonDto updatePersonName(Integer id, String name) {
		Person person = personRepository.findById(id).orElseThrow(PersonNotFoundException::new);
		person.setName(name);
		return modelMapper.map(person, checkDtoClass(person));
	}

	@Override
	@Transactional
	public PersonDto updatePersonAddress(Integer id, AddressDto addressDto) {
		Person person = personRepository.findById(id).orElseThrow(PersonNotFoundException::new);
		person.setAddress(modelMapper.map(addressDto, Address.class));
		return modelMapper.map(person, checkDtoClass(person));
	}

	@Override
	@Transactional(readOnly = true)
	public Iterable<PersonDto> findPersonsByCity(String city) {
		return personRepository.findByAddressCityIgnoreCase(city).map(p -> modelMapper.map(p, checkDtoClass(p)))
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public Iterable<PersonDto> findPersonsByName(String name) {
		return personRepository.findByNameIgnoreCase(name).map(p -> modelMapper.map(p, checkDtoClass(p)))
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public Iterable<PersonDto> findPersonsBetweenAge(Integer minAge, Integer maxAge) {
		LocalDate from = LocalDate.now().minusYears(maxAge);
		LocalDate to = LocalDate.now().minusYears(minAge);
		return personRepository.findByBirthDateBetween(from, to).map(p -> modelMapper.map(p, checkDtoClass(p)))
				.collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public Iterable<CityPopulationDto> getCitiesPopulation() {
		return personRepository.getCitiesPopulation().collect(Collectors.toList());
	}
	
	@Override
	@Transactional(readOnly = true)
	public Iterable<PersonDto> findAllChildren() {
		return personRepository.findAllChildren().map(p -> modelMapper.map(p, checkDtoClass(p)))
				.collect(Collectors.toList());
	}
	
	@Override
	@Transactional(readOnly = true)
	public Iterable<PersonDto> findEmployeesBySalary(Integer minSalary, Integer maxSalary) {
		return personRepository.findBySalaryBetween(minSalary, maxSalary).map(p -> modelMapper.map(p, checkDtoClass(p)))
				.collect(Collectors.toList());
	}


	@Override
	@Transactional
	public void run(String... args) throws Exception {
		if (personRepository.count() == 0) {
			Person person = new Person(1000, "John", LocalDate.of(1985, 4, 11),
					new Address("Tel Aviv", "Ben Gvirol", 87));
			Child child = new Child(2000, "Mosche", LocalDate.of(2018, 7, 5), new Address("Ashkelon", "Bar Kohva", 21),
					"Shalom");
			Employee employee = new Employee(3000, "Sarah", LocalDate.of(1995, 11, 23),
					new Address("Rehovot", "Herzl", 7), "Motorola", 20_000);
			personRepository.save(person);
			personRepository.save(child);
			personRepository.save(employee);
		}

	}

	
	
	

}