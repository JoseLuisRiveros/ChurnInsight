package com.grupoonce.ChurnInsight.repository;

import org.springframework.data.jpa.repository.Query;

public interface PredictionRepository extends JpaRepository<Prediccion, Long>{
    //si en algun momento se necesita ver los cambios en las predicciones en el tiempo(requiere many to
    // one de parte de la entidad de Prediccion a la de Entrada)
    List<Prediccion> findByEntradaId(Long entradaId);

    //suponiendo que en algun momento se va a guardar los resultados de la prediccion
    @Query("SELECT p FROM Prediccion p WHERE p.resultado = 'ALTO'")
    List<Prediccion> findAltoRiesgo();

}
