"""
Microservicio de predicción con FastAPI
Carga un modelo de Machine Learning y expone un endpoint para realizar predicciones
"""

from fastapi import FastAPI, HTTPException
from fastapi.responses import JSONResponse
from pydantic import BaseModel, ValidationError
import pickle
import os
from typing import Dict, Any, List
import logging

# Configurar logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Inicializar FastAPI
app = FastAPI(
    title="ChurnInsight Prediction Service",
    description="Microservicio para predicciones usando modelos de Machine Learning",
    version="1.0.0"
)

# Variable global para almacenar el modelo
model = None
model_path = "modelo.pkl"


class PredictionRequest(BaseModel):
    """Modelo de datos para la solicitud de predicción"""
    data: Dict[str, Any] | List[Dict[str, Any]]


class PredictionResponse(BaseModel):
    """Modelo de respuesta para la predicción"""
    status: str
    message: str
    prediction: Any = None
    predictions: List[Any] = None


def load_model():
    """
    Carga el modelo desde el archivo .pkl
    Se ejecuta al iniciar la aplicación
    """
    global model
    
    if not os.path.exists(model_path):
        logger.error(f"Modelo no encontrado en: {model_path}")
        raise FileNotFoundError(
            f"El archivo del modelo '{model_path}' no existe. "
            "Por favor, coloca tu modelo entrenado en la raíz del proyecto."
        )
    
    try:
        with open(model_path, 'rb') as f:
            model = pickle.load(f)
        logger.info(f"Modelo cargado exitosamente desde: {model_path}")
        logger.info(f"Tipo de modelo: {type(model)}")
    except Exception as e:
        logger.error(f"Error al cargar el modelo: {str(e)}")
        raise Exception(f"Error al cargar el modelo: {str(e)}")


@app.on_event("startup")
async def startup_event():
    """Evento que se ejecuta al iniciar la aplicación"""
    logger.info("Iniciando microservicio de predicción...")
    try:
        load_model()
        logger.info("Microservicio listo para recibir solicitudes")
    except Exception as e:
        logger.error(f"Error crítico al iniciar: {str(e)}")
        # No lanzamos excepción aquí para que la app pueda iniciar
        # pero el endpoint /predict manejará el error


@app.get("/")
async def root():
    """Endpoint raíz para verificar que el servicio está funcionando"""
    return {
        "status": "ok",
        "message": "Microservicio de predicción activo",
        "model_loaded": model is not None
    }


@app.get("/health")
async def health_check():
    """Endpoint de salud para verificar el estado del servicio"""
    return {
        "status": "healthy" if model is not None else "unhealthy",
        "message": "Modelo cargado correctamente" if model is not None else "Modelo no cargado",
        "model_loaded": model is not None
    }


@app.post("/predict", response_model=PredictionResponse)
async def predict(request: PredictionRequest):
    """
    Endpoint para realizar predicciones
    
    Args:
        request: Objeto con los datos de entrada en formato JSON
        
    Returns:
        JSON con la predicción y estado
        
    Ejemplo de uso:
        POST /predict
        {
            "data": {
                "feature1": 1.0,
                "feature2": 2.0,
                ...
            }
        }
        
        O para múltiples predicciones:
        {
            "data": [
                {"feature1": 1.0, "feature2": 2.0},
                {"feature1": 3.0, "feature2": 4.0}
            ]
        }
    """
    # Verificar que el modelo esté cargado
    if model is None:
        logger.error("Intento de predicción sin modelo cargado")
        raise HTTPException(
            status_code=503,
            detail="El modelo no está disponible. Por favor, verifica que el archivo modelo.pkl existe."
        )
    
    try:
        input_data = request.data
        
        # Determinar si es una predicción única o múltiple
        is_batch = isinstance(input_data, list)
        
        if is_batch:
            # Predicción por lotes
            if len(input_data) == 0:
                raise HTTPException(
                    status_code=400,
                    detail="La lista de datos está vacía"
                )
            
            # Convertir lista de diccionarios a formato adecuado para el modelo
            # Asumimos que el modelo espera un array 2D
            try:
                # Intentar convertir a formato numpy/array si es necesario
                import numpy as np
                
                # Extraer características en el mismo orden
                if len(input_data) > 0:
                    feature_names = list(input_data[0].keys())
                    X = np.array([[item.get(feat, 0) for feat in feature_names] 
                                 for item in input_data])
                else:
                    X = np.array([])
                
                predictions = model.predict(X).tolist()
                
                return PredictionResponse(
                    status="success",
                    message=f"Predicciones realizadas exitosamente para {len(predictions)} registros",
                    predictions=predictions
                )
            except Exception as e:
                logger.error(f"Error en predicción por lotes: {str(e)}")
                raise HTTPException(
                    status_code=500,
                    detail=f"Error al procesar predicción por lotes: {str(e)}"
                )
        else:
            # Predicción única
            try:
                # Convertir diccionario a formato adecuado para el modelo
                import numpy as np
                
                # Extraer características en orden
                feature_names = list(input_data.keys())
                X = np.array([[input_data[feat] for feat in feature_names]])
                
                prediction = model.predict(X)[0]
                
                # Si la predicción es un array, convertir a lista o valor escalar
                if hasattr(prediction, 'tolist'):
                    prediction = prediction.tolist()
                elif hasattr(prediction, 'item'):
                    prediction = prediction.item()
                
                return PredictionResponse(
                    status="success",
                    message="Predicción realizada exitosamente",
                    prediction=prediction
                )
            except Exception as e:
                logger.error(f"Error en predicción única: {str(e)}")
                raise HTTPException(
                    status_code=500,
                    detail=f"Error al procesar la predicción: {str(e)}"
                )
    
    except HTTPException:
        # Re-lanzar excepciones HTTP
        raise
    except ValidationError as e:
        logger.error(f"Error de validación: {str(e)}")
        raise HTTPException(
            status_code=422,
            detail=f"Datos de entrada inválidos: {str(e)}"
        )
    except Exception as e:
        logger.error(f"Error inesperado: {str(e)}")
        raise HTTPException(
            status_code=500,
            detail=f"Error interno del servidor: {str(e)}"
        )


@app.exception_handler(404)
async def not_found_handler(request, exc):
    """Manejo de errores 404"""
    return JSONResponse(
        status_code=404,
        content={"status": "error", "message": "Endpoint no encontrado"}
    )


@app.exception_handler(500)
async def internal_error_handler(request, exc):
    """Manejo de errores 500"""
    return JSONResponse(
        status_code=500,
        content={"status": "error", "message": "Error interno del servidor"}
    )

