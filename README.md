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

---

## Keycloak en Kubernetes

### Requisitos previos

⚠️ **PostgreSQL debe estar levantado y funcionando** antes de desplegar Keycloak. Keycloak requiere una base de datos para almacenar su configuración.

### 1) Crear secreto para la base de datos de Keycloak

```bash
kubectl create secret generic keycloak-db-secret --from-literal=username=keycloak --from-literal=password=miContraseñaSecreta
```

- Crea un secreto genérico llamado `keycloak-db-secret` con las credenciales de base de datos.
- Usuario: `keycloak`
- Contraseña: `miContraseñaSecreta`

### 2) Crear usuario y base de datos en PostgreSQL

Conecta al pod de PostgreSQL y ejecuta los siguientes comandos SQL:

```bash
kubectl exec -it postgres-0 -- psql -U postgres
```

Dentro de la sesión PostgreSQL, ejecuta:

```sql
CREATE USER keycloak WITH PASSWORD 'miContraseñaSecreta';
CREATE DATABASE keycloak;
ALTER DATABASE keycloak OWNER TO keycloak;
GRANT ALL PRIVILEGES ON DATABASE keycloak TO keycloak;
```

- Crea un usuario `keycloak` con la contraseña especificada.
- Crea la base de datos `keycloak` y la asigna al usuario `keycloak`.
- Otorga todos los permisos al usuario sobre la base de datos.

### 3) Aplicar manifiestos de Keycloak

```bash
kubectl apply -f .\k8s\keycloak.yml
```

- Esto despliega Keycloak en el clúster junto con su configuración.

### 4) Verificar estado de pods de Keycloak

```bash
kubectl get pods -l app=keycloak
```

- Espera a que el pod de Keycloak esté en estado `Running`.
- Si hay problemas, usa:
  - `kubectl describe pod <nombre> -l app=keycloak`
  - `kubectl logs -l app=keycloak`

### 5) Acceder a Keycloak

#### Opción A: Sin Traefik levantado

Realiza un port-forward local:

```bash
kubectl port-forward service/keycloak 8080:8080
```

- Esto expone el servicio Keycloak en `http://localhost:8080`.

#### Opción B: Con Traefik levantado

Si tienes Traefik configurado, accede directamente a través del dominio:

```
http://auth.127.0.0.1.nip.io
```

- No necesitas port-forward, Traefik enruta automáticamente las peticiones al servicio de Keycloak.

### 6) Acceder al panel de administración

- Usuario: `admin`
- Contraseña: `admin`

### 7) Primeras acciones recomendadas

Una vez logueado en el panel de administración:

1. **Cambiar la contraseña de `admin`**: Ve a `Admin` (menú superior derecha) → `Manage account` → `Signing in` → `Change password`.

2. **Comprobar la creación del realm**: Un Realm ("reino") es un dominio de seguridad aislado que actúa como un contenedor lógico para gestionar un conjunto de usuarios, credenciales, roles, grupos y configuraciones de acceso. Es la unidad fundamental de aislamiento en Keycloak, nuestra aplicación usa un realm llamado "slotify", al seleccionar slotify, en clients/clientScopes debemos tener frontend-dedicated en el cual tenemos el atributo account-type que usaremos para identificar el tipo de usuario.

---

## Traefik en Kubernetes

### Requisitos previos

⚠️ **Traefik es un ingress controller y reverse proxy** necesario para enrutar tráfico HTTP/HTTPS hacia servicios internos del clúster como Keycloak y Spring API a través de dominios personalizados.

### 1) Instalar CRDs de Traefik

Traefik v3.0 requiere Custom Resource Definitions para recursos como `IngressRoute` y `Middleware`:

```bash
kubectl apply -f https://raw.githubusercontent.com/traefik/traefik/v3.0/docs/content/reference/dynamic-configuration/kubernetes-crd-definition-v1.yml
```

- Registra en Kubernetes los CustomResourceDefinitions de Traefik (`IngressRoute`, `Middleware`, `TLSOptions`, etc.).
- Estos recursos permiten configurar enrutamiento avanzado sin usar el `Ingress` estándar de Kubernetes.

### 2) Aplicar RBAC para Traefik

Traefik necesita permisos para acceder a recursos del clúster:

```bash
kubectl apply -f https://raw.githubusercontent.com/traefik/traefik/v3.0/docs/content/reference/dynamic-configuration/kubernetes-crd-rbac.yml
```

- Define un `ServiceAccount`, `ClusterRole` y `ClusterRoleBinding` que permiten a Traefik leer y monitorear:
  - Servicios (`services`)
  - Nodos (`nodes`)
  - Secretos (`secrets`)
  - Recursos de Traefik (`ingressroutes`, `middlewares`, etc.)
  - Ingresses estándar de Kubernetes

### 3) Aplicar manifiestos locales en `k8s/`

```bash
kubectl apply -f .\k8s\
```

- Aplica los manifiestos de Traefik locales, incluyendo:
  - `Deployment` de Traefik v3.0 con el contenedor de Traefik
  - `Service` de tipo `LoadBalancer` para exponer puertos 80 (HTTP) y 443 (HTTPS)
  - `IngressRoutes` para Keycloak y Spring API
  - `Middleware` personalizado para control de acceso (whitelist de IPs)

### 4) Verificar estado de pods de Traefik

```bash
kubectl get pods -l app=traefik
```

- Espera a que el pod de Traefik esté en estado `1/1` y `Running`.
- Si hay problemas, usa:
  - `kubectl describe pod <nombre> -l app=traefik`
  - `kubectl logs -l app=traefik`

### 5) Ver servicios creados

```bash
kubectl get svc | grep traefik
```

- Busca el servicio `traefik` de tipo `LoadBalancer`.
- En desarrollo local (minikube/Docker Desktop), la columna `EXTERNAL-IP` puede mostrar `<pending>` o `localhost`.

### 6) Verificar IngressRoutes configuradas

```bash
kubectl get ingressroute
```

- Lista todas las `IngressRoutes` registradas.
- Deberías ver al menos dos rutas:
  - `keycloak`: enruta `auth.127.0.0.1.nip.io` hacia el servicio Keycloak
  - `spring-api`: enruta `api.127.0.0.1.nip.io` hacia el servicio Spring API

Detalle de una IngressRoute específica:

```bash
kubectl describe ingressroute keycloak
```

- Muestra los detalles de enrutamiento: hosts, middlewares aplicados, servicios destino.

### 7) Verificar Middlewares

```bash
kubectl get middleware
```

- Lista los middlewares configurados.
- El middleware `block-admin` restringe el acceso a `/admin` a direcciones IP en whitelist (redes privadas locales):
  - `10.0.0.0/8`
  - `127.0.0.1/32`
  - `172.16.0.0/24`
  - `192.168.0.0/16`

### 8) Acceder a través de dominios personalizados

En tu máquina local, Traefik expone los servicios a través de dominios de nip.io:

**Keycloak:**
```
http://auth.127.0.0.1.nip.io
```

- Acceso público a Keycloak.

**Keycloak Admin (restringido):**
```
http://auth.127.0.0.1.nip.io/admin
```

- Requiere una IP en la whitelist del middleware `block-admin`.
- Si accedes desde `127.0.0.1`, deberías poder entrar.
- Si accedes desde otra red, recibirás error 403.

**Spring API:**
```
http://api.127.0.0.1.nip.io
```

- Acceso a la API del backend.

### 9) Verificar conectividad del Traefik

Para asegurar que Traefik está escuchando correctamente:

```bash
kubectl port-forward service/traefik 8080:80
```

- Esto abre un túnel local al puerto 80 de Traefik en `http://localhost:8080`.
- Prueba accediendo a un dominio en el navegador o con curl:

```bash
curl -H "Host: auth.127.0.0.1.nip.io" http://localhost:8080
```

- Si todo funciona, recibirás respuesta de Keycloak.

### 10) Verificar logs de enrutamiento

```bash
kubectl logs -l app=traefik
```

- Muestra los logs de Traefik, incluyendo:
  - Rutas registradas
  - Requests procesados
  - Errores de conectividad

Puedes seguir los logs en tiempo real:

```bash
kubectl logs -f -l app=traefik
```

### Troubleshooting común

| Problema | Solución |
|----------|----------|
| `Connection refused` al acceder a dominios | Verifica que Traefik esté en estado `Running` y que el servicio esté expuesto. |
| `503 Service Unavailable` | El servicio de destino (Keycloak, Spring API) no está disponible. Verifica con `kubectl get svc`. |
| `403 Forbidden` en `/admin` | Tu IP no está en la whitelist del middleware. Comprueba la configuración de `sourceRange`. |
| Traefik no detecta nuevas IngressRoutes | Verifica que el RBAC tenga permisos para leer `ingressroutes`: `kubectl describe clusterrole traefik-ingress-controller`. |
| DNS no resuelve `*.127.0.0.1.nip.io` | Asegúrate de que nip.io está disponible. Alternativa: configura `/etc/hosts` o `C:\Windows\System32\drivers\etc\hosts` manualmente. |

---

## Spring Backend en Kubernetes

### Requisitos previos

⚠️ **Los siguientes servicios deben estar levantados y funcionando** antes de desplegar Spring:
- PostgreSQL (base de datos)
- Redis (caché)
- Keycloak (autenticación)
- Elasticsearch + Kibana (búsqueda y logs)
- Ollama (modelos de IA)

### 1) Construir la imagen Docker del backend Spring

Desde la raíz del proyecto, ejecuta:

```bash
docker build -t slotify-backend:latest -f ./backend/slotify/Dockerfile .
```

- Construye una imagen Docker con el tag `slotify-backend:latest`.
- El Dockerfile se encuentra en `./backend/slotify/Dockerfile`.
- Esta imagen contiene la aplicación Spring compilada y lista para ejecutarse en un contenedor.

### 2) Aplicar manifiestos de Spring en Kubernetes

```bash
kubectl apply -f .\k8s\
```

- Aplica todos los manifiestos en la carpeta `k8s`, incluyendo:
  - `Deployment` de Spring que usa la imagen `slotify-backend:latest`
  - `Service` de tipo `ClusterIP` para exponer el backend dentro del clúster
  - `IngressRoute` de Traefik para enrutar `api.127.0.0.1.nip.io` hacia Spring
  - `ConfigMaps` y `Secrets` con variables de entorno necesarias (credenciales de BD, Keycloak, Redis)

### 3) Verificar estado de pods de Spring

```bash
kubectl get pods -l app=spring
```

- Espera a que el pod de Spring esté en estado `1/1` y `Running`.
- Si hay problemas, usa:
  - `kubectl describe pod <nombre> -l app=spring`
  - `kubectl logs <nombre> -l app=spring`

### 4) Ver servicios creados

```bash
kubectl get svc | grep spring
```

- Busca el servicio `spring` de tipo `ClusterIP`.
- Verifica que el puerto sea el correcto (por defecto `8080`).

### 5) Acceder a la API de Spring

Con Traefik levantado:

```
http://api.127.0.0.1.nip.io
```

- Acceso a la API del backend directamente a través del dominio de Traefik.

Sin Traefik levantado:

```bash
kubectl port-forward service/spring 8080:8080
```

- Abre un túnel local a `http://localhost:8080`.

### 6) Verificar logs de Spring

```bash
kubectl logs -l app=spring
```

- Muestra los logs de Spring Boot, incluyendo:
  - Inicialización de la aplicación
  - Conexiones a bases de datos
  - Errores de autenticación o integración

Seguir logs en tiempo real:

```bash
kubectl logs -f -l app=spring
```

### 7) Troubleshooting común

| Problema | Solución |
|----------|----------|
| `Connection refused` a PostgreSQL | Verifica que PostgreSQL esté en estado `Running`: `kubectl get pods -l app=postgres`. |
| `Connection refused` a Redis | Verifica que Redis esté en estado `Running`: `kubectl get pods -l app=redis`. |
| Error de autenticación con Keycloak | Verifica que Keycloak esté levantado y que las credenciales en el ConfigMap de Spring sean correctas. |
| `ImagePullBackOff` | La imagen `slotify-backend:latest` no se encontró. Reconstruye la imagen con el comando: `docker build -t slotify-backend:latest -f ./backend/slotify/Dockerfile .` |
| Spring no se conecta a la BD | Verifica el `Secret` y `ConfigMap` con las credenciales de PostgreSQL: `kubectl get secret` y `kubectl get configmap`. |
| `503 Service Unavailable` en Traefik | Verifica que el selector de labels en el `IngressRoute` coincida con las etiquetas del pod de Spring: `kubectl describe pod <nombre> \| grep Labels`. |
