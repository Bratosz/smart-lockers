package pl.bratosz.smartlockers.model;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EmployeeTest {

    @Autowired
    private Validator validator;

    @Test
    public void shouldValidateIncorrectUserName() throws Exception {
        //given
        String firstName = "Andrzej1";
        String lastName = "Nowak";
        Department department = Department.COMMON;
        //when
        Employee employee = new Employee(firstName, lastName, department);
        Set<ConstraintViolation<Employee>> violations = validator.validate(employee);
        //then
        assertEquals(1, violations.size());
    }
}
