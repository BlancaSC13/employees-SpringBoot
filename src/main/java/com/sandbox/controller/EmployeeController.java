package com.sandbox.controller;

import com.sandbox.model.Employee;
import com.sandbox.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.util.List;

@RestController //change in case you want to test the monolithic app
@RequestMapping(path = "/api/employee")
@CrossOrigin(origins = "http://localhost:4200")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/")
    public String viewHomePage(Model model) {
        return findPaginated(1, "firstName", "asc", model);
    }

    @PostMapping("/saveEmployee")
    //ModelAttribute es como [FromBody]
    public String saveEmployee(@ModelAttribute("employee") Employee employee) {

        employeeService.saveEmployee(employee);

        return "redirect:/";
    }

    //TODO: test this with DeleteMapping
    @PostMapping ("/deleteEmployee")
    public void deleteEmployeeById(@RequestParam("employeeId") long employeeId) {
        employeeService.deleteEmployeeById(employeeId);
    }

    @GetMapping("/getAllEmployee")
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }


    @GetMapping("/showNewEmployeeForm")
    public String showNewEmployeeForm(Model model) {
        //create model attribute to bind form data
        Employee employee = new Employee();
        model.addAttribute("employee", employee);
        return "new_employee";
    }

    @GetMapping("/showFormUpdate/{id}")
    //ModelAttribute es como [FromBody]
    public String showFormUpdate(@PathVariable(value = "id") long id, Model model) {
        //get employee drom service (db)
        Employee employee = employeeService.getEmployeeById(id);
        model.addAttribute("employee", employee);
        //set employee as a model attribute to populate the form
        return "update_employee";
    }

    @GetMapping("/page/{pageNo}")
    public String findPaginated(@PathVariable(value = "pageNo") int pageNo,
                                @RequestParam("sortField") String sortField,
                                @RequestParam("sortDir") String sortDir,
                                Model model) {
        int pageSize = 5;

        Page<Employee> page = employeeService.findPaginated(pageNo, pageSize, sortField, sortDir);
        List<Employee> listEmployees = page.getContent();

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", page.getTotalPages());
        model.addAttribute("totalItems", page.getTotalElements());

        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        model.addAttribute("listEmployees", listEmployees);
        return "index";
    }

}