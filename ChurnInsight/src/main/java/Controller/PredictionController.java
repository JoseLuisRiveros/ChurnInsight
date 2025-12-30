package Controller;

import Entity.Customer;
import Entity.Prediction;
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
@RequestMapping("/predictions")
public class PredictionController {

    @Autowired
    private PredictionRepository repository;

    @Transactional
    @PostMapping
    public void registrar(@RequestBody @Valid PredictionResponse pr){
        repository.save(new Prediction(pr));
    }

    @GetMapping

    public Page<PredictionResponse>listar(@PageableDefault(size=10) Pageable paginacion){
        return repository.findAll(paginacion).map(PredictionResponse::new);
    }

    @GetMapping ("/{id}")
    public ResponseEntity<PredictionResponse> detallar(@PathVariable String customer_id){
        var prediction = repository.findById(customer_id)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Prediction no encontrado"));
        PredictionResponse pr = new PredictionResponse(pr);
        return ResponseEntity.ok(pr);
    }

    @PutMapping
    public void actualizar(@RequestBody @Valid PredictionResponse  pr){
        var prediction = repository.getReferenceById(pr.customer_id());
        prediction.actualizarInformacion(pr);
    }


    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable String customer_id){
        repository.deleteById(customer_id);
    }
}
