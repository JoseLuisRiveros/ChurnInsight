# app.py
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import joblib
import pandas as pd
import uvicorn
from pathlib import Path
import logging

logging.basicConfig(level=logging.INFO)

# Intentamos cargar el modelo; si no existe, dejamos `model = None`
MODEL_PATH = Path("churn_model_simple.joblib")
model = None
try:
    if MODEL_PATH.exists():
        model = joblib.load(MODEL_PATH)
        logging.info(f"Modelo cargado desde: {MODEL_PATH}")
    else:
        logging.warning(f"Archivo de modelo no encontrado: {MODEL_PATH}")
        model = None
except Exception as e:
    logging.exception("Error al cargar el modelo:")
    model = None

app = FastAPI()

# Definir la estructura de datos que enviará JAVA
class CustomerData(BaseModel):
    tenure: int
    usage_time: float
    login_frequency: float
    total_spend: float
    contract_type: str        # Ej: "Two Year", "Month-to-Month"
    subscription_type: str    # Ej: "Premium", "Basic"
    payment_record: str       # Ej: "Good", "Delayed"


@app.post("/predict")
def predict_churn(data: CustomerData):
    # Si el modelo no está disponible, devolvemos 503
    if model is None:
        raise HTTPException(status_code=503, detail=f"Modelo no cargado. Comprueba que '{MODEL_PATH}' existe y es accesible.")

    # Preprocesamiento: convertir entradas categóricas a los indicadores que espera el modelo
    contract_val = 1 if data.contract_type == "Two Year" else 0
    sub_val = 1 if data.subscription_type == "Premium" else 0
    pay_val = 1 if data.payment_record == "Good" else 0

    input_df = pd.DataFrame([{
        'tenure': data.tenure,
        'usage_time': data.usage_time,
        'login_frequency': data.login_frequency,
        'total_spend': data.total_spend,
        # 'churn' se incluye solo por estructura si realmente lo necesitas; no es usado para predecir
        'churn': 0,
        'contract_type_two year': contract_val,
        'subscription_type_premium': sub_val,
        'payment_record_good': pay_val
    }])

    features = input_df[[
        'tenure', 'usage_time', 'login_frequency', 'total_spend',
        'contract_type_two year', 'subscription_type_premium', 'payment_record_good'
    ]]

    prediction = model.predict(features)[0]
    probability = model.predict_proba(features)[0][1]

    return {
        "prediction": "HIGH_RISK" if prediction == 1 else "LOW_RISK",
        "probability": float(probability),
        "status": "success"
    }


# Para correrlo: uvicorn app:app --reload
if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)