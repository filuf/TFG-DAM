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

- Abre localmente `https://localhost:5601`.
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

- URL: `https://localhost:5601`
- Usuario: `elastic`
- Contraseña: valor obtenido en el paso anterior.
---

