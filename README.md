# TFG-DAM

## Elasticsearch + ECK + Kibana en Kubernetes (instrucciones completas)

Este documento explica cómo desplegar Elastic Cloud on Kubernetes (ECK) y cómo acceder a Kibana por HTTPS usando el usuario `elastic`.

### 1) Instalar CRDs y Operator de ECK

```bash
kubectl create -f https://download.elastic.co/downloads/eck/3.3.1/crds.yaml
kubectl apply -f https://download.elastic.co/downloads/eck/3.3.1/operator.yaml
```

- `kubectl create -f .../crds.yaml`: registra en Kubernetes los CustomResourceDefinitions de Elastic (Elasticsearch, Kibana, etc.).
- `kubectl apply -f .../operator.yaml`: despliega el controlador/operator de ECK en el clúster.

### 2) Aplicar manifiestos locales en `k8s/`

```bash
kubectl apply -f .\k8s\
```

- Aplica todos los manifiestos en la carpeta `k8s`.
- Se espera que incluya los CRs de Elasticsearch y Kibana, además de secretos/configmaps.

### 3) Verificar estado de pods

```bash
kubectl get pods
```

- Espera `Running` (u `Completed` según tipo de pod).
- Si hay `Pending` o `CrashLoopBackOff`:
  - `kubectl describe pod <nombre>`
  - `kubectl logs <nombre>`

### 4) Ver servicios creados

```bash
kubectl get svc
```

- Verifica los servicios como `elastic-es-http`, `kibana-kb-http`.

### 5) Port-forward a Kibana local

```bash
kubectl port-forward service/kibana-kb-http 5601:5601
```

- Abre localmente `http://localhost:5601`.
- Por defecto Kibana usa HTTPS con certificado autofirmado.

### 6) Obtener contraseña del usuario `elastic`

```bash
BASH

kubectl get secret elastic-es-elastic-user -o=jsonpath='{.data.elastic}' | base64 --decode


POWERSHELL

$secret = kubectl get secret elastic-es-elastic-user -o jsonpath='{.data.elastic}'
[System.Text.Encoding]::UTF8.GetString([System.Convert]::FromBase64String($secret)) -split '\\' | Select-Object -First 1
```

- Esto decodifica la contraseña almacenada en el secreto `elastic-es-elastic-user`.

### 7) Iniciar sesión en Kibana

- URL: `http://localhost:5601`
- Usuario: `elastic`
- Contraseña: valor obtenido en el paso anterior.
---

## Ollama en Kubernetes

### 1) Aplicar manifiestos de Ollama

```bash
kubectl apply -f .\k8s\
```

- Esto despliega los recursos definidos para Ollama en el namespace correspondiente.

### 2) Verificar estado de pods de Ollama

```bash
kubectl get pods -n ollama
```

- Espera a que el pod de Ollama esté en `1/1` y `Running`.
- Si hay problemas, usa `kubectl describe pod <nombre> -n ollama` o `kubectl logs <nombre> -n ollama`.

### 3) Conectar al pod de Ollama

```bash
kubectl exec -it mi-ollama -n ollama -- /bin/bash
```

- Reemplaza `mi-ollama` por el nombre real del pod si es diferente.

### 4) Descargar el modelo en Ollama

Dentro del pod de Ollama:

```bash
ollama pull llama3:instruct
```

- Esto descarga el modelo `llama3` con la variante `instruct` para su uso.

### 5) Ejecutar Ollama localmente

```bash
ollama run llama3
```

- Inicia el servicio de Ollama con el modelo cargado.

### 6) Hacer port-forward para acceder desde fuera

```bash
kubectl port-forward -n ollama svc/ollama 11434:11434
```

- Esto expone el servicio Ollama en `http://localhost:11434`.

### 7) Interactuar con Ollama vía API HTTP

Realiza una petición POST a `http://localhost:11434/api/generate`.

Ejemplo de body JSON:

```json
{
    "model": "llama3",
    "prompt": "cuanto es 5 + 5",
    "stream": false
}
```

- La respuesta contendrá el texto generado por el modelo.
- Si usas `curl`, puedes enviar la petición así:

```bash
curl -X POST http://localhost:11434/api/generate \
  -H "Content-Type: application/json" \
  -d '{"model":"llama3","prompt":"cuanto es 5 + 5","stream":false}'
```

---

## PostgreSQL en Kubernetes

### 1) Crear ConfigMap con el script de inicialización

```bash
kubectl create configmap postgres-init --from-file=./db/reserves_init_pg.sql
```

- Crea un ConfigMap llamado `postgres-init` que contiene el archivo `reserves_init_pg.sql` de la carpeta `db/`.

### 2) Crear secreto para la contraseña de PostgreSQL

```bash
kubectl create secret generic postgres-secret --from-literal=password=miContraseñaSecreta
```

- Crea un secreto genérico llamado `postgres-secret` con la contraseña para el usuario de PostgreSQL.

### 3) Aplicar manifiestos locales en `k8s/`

```bash
kubectl apply -f ./k8s/
```

- Aplica todos los manifiestos en la carpeta `k8s`, incluyendo el StatefulSet y Service para PostgreSQL.

### 4) Esperar a que el pod esté levantado

- Espera a que el pod de PostgreSQL esté en estado `Running`.

### 5) Verificar estado de pods de PostgreSQL

```bash
kubectl get pods -l app=postgres
```

- Lista los pods etiquetados con `app=postgres` para verificar que esté corriendo.

### 6) En caso de error, ejecutar el script de inicialización manualmente

Si hay problemas con la inicialización automática, puedes ejecutar el script manualmente:

```bash
kubectl exec -it postgres-0 -- psql -U postgres -d appdb
```

Dentro del pod, ejecuta:

```sql
\i /docker-entrypoint-initdb.d/reserves_init_pg.sql
```

### 7) Dropear el esquema si es necesario

Si necesitas reiniciar el esquema:

```sql
DROP SCHEMA IF EXISTS reserves CASCADE;
```

- Esto elimina el esquema `reserves` y todos sus objetos dependientes.

