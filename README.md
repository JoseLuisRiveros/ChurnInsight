# Microservicio de Predicción - ChurnInsight

Microservicio en Python usando FastAPI para realizar predicciones con modelos de Machine Learning previamente entrenados.

## Características

- ✅ Endpoint POST `/predict` para realizar predicciones
- ✅ Carga automática del modelo al iniciar el servidor
- ✅ Soporte para predicciones individuales y por lotes
- ✅ Manejo robusto de errores
- ✅ Validación de datos de entrada
- ✅ Endpoints de salud y estado

## Instalación

1. Instalar las dependencias:
```bash
pip install -r requirements.txt
```

2. Colocar tu modelo entrenado en la raíz del proyecto con el nombre `modelo.pkl`

## Uso

### Iniciar el servidor

```bash
uvicorn main:app --reload
```

El servidor estará disponible en `http://localhost:8000`

### Documentación interactiva

Una vez iniciado el servidor, puedes acceder a:
- **Swagger UI**: http://localhost:8000/docs
- **ReDoc**: http://localhost:8000/redoc

## Endpoints

### GET `/`
Endpoint raíz para verificar que el servicio está funcionando.

**Respuesta:**
```json
{
  "status": "ok",
  "message": "Microservicio de predicción activo",
  "model_loaded": true
}
```

### GET `/health`
Endpoint de salud para verificar el estado del servicio y si el modelo está cargado.

**Respuesta:**
```json
{
  "status": "healthy",
  "message": "Modelo cargado correctamente",
  "model_loaded": true
}
```

### POST `/predict`
Endpoint principal para realizar predicciones.

#### Predicción única

**Request:**
```json
{
  "data": {
    "feature1": 1.0,
    "feature2": 2.0,
    "feature3": 3.0
  }
}
```

**Response:**
```json
{
  "status": "success",
  "message": "Predicción realizada exitosamente",
  "prediction": 0.85
}
```

#### Predicción por lotes

**Request:**
```json
{
  "data": [
    {
      "feature1": 1.0,
      "feature2": 2.0,
      "feature3": 3.0
    },
    {
      "feature1": 4.0,
      "feature2": 5.0,
      "feature3": 6.0
    }
  ]
}
```

**Response:**
```json
{
  "status": "success",
  "message": "Predicciones realizadas exitosamente para 2 registros",
  "predictions": [0.85, 0.92]
}
```

## Ejemplo de uso con cURL

```bash
# Predicción única
curl -X POST "http://localhost:8000/predict" \
     -H "Content-Type: application/json" \
     -d '{
       "data": {
         "feature1": 1.0,
         "feature2": 2.0,
         "feature3": 3.0
       }
     }'

# Predicción por lotes
curl -X POST "http://localhost:8000/predict" \
     -H "Content-Type: application/json" \
     -d '{
       "data": [
         {"feature1": 1.0, "feature2": 2.0, "feature3": 3.0},
         {"feature1": 4.0, "feature2": 5.0, "feature3": 6.0}
       ]
     }'
```

## Ejemplo de uso con Python

```python
import requests

# Predicción única
response = requests.post(
    "http://localhost:8000/predict",
    json={
        "data": {
            "feature1": 1.0,
            "feature2": 2.0,
            "feature3": 3.0
        }
    }
)
print(response.json())

# Predicción por lotes
response = requests.post(
    "http://localhost:8000/predict",
    json={
        "data": [
            {"feature1": 1.0, "feature2": 2.0, "feature3": 3.0},
            {"feature1": 4.0, "feature2": 5.0, "feature3": 6.0}
        ]
    }
)
print(response.json())
```

## Manejo de Errores

El microservicio maneja los siguientes errores:

- **400 Bad Request**: Datos de entrada inválidos o lista vacía
- **422 Unprocessable Entity**: Error de validación de Pydantic
- **503 Service Unavailable**: Modelo no cargado o no disponible
- **500 Internal Server Error**: Error interno del servidor

## Notas Importantes

1. **Formato del modelo**: El modelo debe estar guardado en formato `.pkl` usando `pickle`
2. **Estructura de datos**: Asegúrate de que las características (features) en el JSON coincidan con las que espera tu modelo
3. **Orden de características**: El código extrae las características en el orden en que aparecen en el diccionario. Si tu modelo requiere un orden específico, asegúrate de enviar los datos en ese orden.

## Personalización

Si tu modelo tiene requisitos específicos (por ejemplo, necesita un preprocesamiento especial o espera los datos en un formato diferente), puedes modificar la función `predict` en `main.py` para adaptarlo a tus necesidades.

