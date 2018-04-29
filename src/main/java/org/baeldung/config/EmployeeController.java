package org.baeldung.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.baeldung.config.model.Customers;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
public class EmployeeController {

    private List<Customers> employees = new ArrayList<>();
    @GetMapping("/employee")
    @ResponseBody
    public Optional<Customers> getEmployee(@RequestParam String email) {
        return employees.stream().filter(x -> x.getEmail().equals(email)).findAny();
    }

    @PostMapping(value = "/employee", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void postMessage(@RequestBody Customers employee) {
        employees.add(employee);
    }

}
