"""
Script para crear un modelo dummy de ejemplo
Úsalo para probar el microservicio si no tienes un modelo propio
"""

import joblib
import numpy as np
from sklearn.ensemble import RandomForestClassifier
from sklearn.datasets import make_classification

# Crear datos de ejemplo
X, y = make_classification(
    n_samples=1000,
    n_features=3,
    n_informative=3,
    n_redundant=0,
    n_classes=2,
    random_state=42
)

# Entrenar un modelo simple
model = RandomForestClassifier(n_estimators=10, random_state=42)
model.fit(X, y)

# Guardar el modelo usando joblib
joblib.dump(model, 'modelo.joblib')

print("✅ Modelo dummy creado exitosamente: modelo.joblib")
print(f"   - Características esperadas: 3 (feature1, feature2, feature3)")
print(f"   - Tipo: RandomForestClassifier")
print("\nEjemplo de datos para probar:")
print('{"data": {"feature1": 0.5, "feature2": -0.3, "feature3": 1.2}}')
