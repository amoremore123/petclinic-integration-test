# Spring Boot : Application PetClinic

## Features  

### 1.  Unit Test  --> Tag v1.0.0
### 2.  Integration Test  --> Tag v2.0.0
mvn clean test -Dspring.profiles.active=h2

## Grupo 1 — OwnerController (`/owners`)

### Aportes disponibles en la rama `leonel`

| Integrante | Rama | Pruebas asignadas | H2 | Mockito |
|---|---|---|---|---|
| A | `yajaira` | `testFindAllOwners`, `testFindOwnerOK`, `testFindOwnerKO` | Completas | Pendientes |
| B | `amore` | `testCreateOwner`, `testCreateOwner_BadRequest`, `testFindByLastName` | Completas | Pendientes |
| C — Leonel | `leonel` | `testUpdateOwner`, `testDeleteOwner`, `testDeleteOwnerKO` | Completas | Completas |

### Estructura de la parte C

- `src/test/java/com/tecsup/petclinic/webs/OwnerControllerTest.java`: integración real contra H2. Crea sus propios propietarios y revierte los cambios con `@Transactional`, sin modificar los datos semilla.
- `src/test/java/com/tecsup/petclinic/webs/OwnerControllerMockitoTest.java`: los mismos tres casos con `@MockitoBean OwnerService`. Verifica el contrato HTTP/JSON y las llamadas al servicio, no la persistencia real.
- `src/test/java/com/tecsup/petclinic/utils/OwnerObjectCreator.java`: construye DTO nuevos en cada llamada para reutilizar datos sin compartir objetos mutables entre pruebas.

La actualización cambia los cinco campos y los confirma con un `GET`. El borrado crea un propietario, espera `200` al eliminarlo y verifica después un `404`. El borrado del ID inexistente `1000` espera `404`.

### Validación

Suite completa (Java 21 y Maven):

```powershell
mvn clean test "-Dspring.profiles.active=h2"
```

Solo las pruebas web de propietarios:

```powershell
mvn test "-Dspring.profiles.active=h2" "-Dtest=OwnerControllerTest,OwnerControllerMockitoTest"
```

Validación del 5 de noviembre de 2026: **39 pruebas, 0 fallos, 0 errores y 0 omitidas** en la suite completa; 12 corresponden a los controladores de propietarios (6 de A/B y 6 de C).

### Integración y conflictos

El trabajo de A y B llegó a `leonel` desde `amore` mediante fast-forward. No se presentaron conflictos durante este aporte de C; no se declara ninguna resolución de conflictos que no haya ocurrido.

Pendientes del equipo: completar Mockito de A/B, registrar una resolución real de conflictos para cumplir la consigna e integrar los aportes a `main` con revisión.
