package Controller;

import Entity.Customer;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.awt.print.Pageable;

@RestController
@RequestMapping("/customers")
public class CustomerController {
    @Autowired
    private CustomerRepository repository;

    @Transactional

    @PostMapping
    public void registrar(@RequestBody @Valid CustomerRequest cr){
        repository.save(new Customer(cr));
    }

    @GetMapping

    public Page<CustomerRequest>listar(@PageableDefault(size=10) Pageable paginacion){
        return repository.findAll(paginacion).map(CustomerRequest::new);
    }

    @GetMapping ("/{id}")
    public ResponseEntity<CustomerRequest>detallar(@PathVariable String customer_id){
        var topico = repository.findById(customer_id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer no encontrado"));
        CustomerRequest cr = new CustomerRequest(cr);
        return ResponseEntity.ok(cr);
    }

    @PutMapping
    public void actualizar(@RequestBody @Valid CustomerRequest  cr){
        var customer = repository.getReferenceById(cr.customer_id());
        customer.actualizarInformacion(cr);
    }


    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable String customer_id){
        repository.deleteById(customer_id);
    }
}
