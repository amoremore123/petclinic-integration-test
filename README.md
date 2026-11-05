# Spring Boot : Application PetClinic

## Features  

### 1.  Unit Test  --> Tag v1.0.0
### 2.  Integration Test  --> Tag v2.0.0
mvn clean test -Dspring.profiles.active=h2

## Grupo 1 — OwnerController (`/owners`)

### Aportes disponibles en la rama `leonel`

| Integrante | Rama | Pruebas asignadas | H2 | Mockito |
|---|---|---|---|---|
| A | `yajaira` | `testFindAllOwners`, `testFindOwnerOK`, `testFindOwnerKO` | Completas | Completas |
| B | `amore` | `testCreateOwner`, `testCreateOwner_BadRequest`, `testFindByLastName` | Completas | Completas |
| C — Leonel | `leonel` | `testUpdateOwner`, `testDeleteOwner`, `testDeleteOwnerKO` | Completas | Completas |

Las pruebas H2 de A/B provienen de sus ramas originales. Sus versiones Mockito se completaron en `leonel`, con commits de Leonel, sin modificar las ramas de A/B ni la autoría de sus aportes originales.

### Estructura de las pruebas

- `src/test/java/com/tecsup/petclinic/webs/OwnerControllerTest.java`: los nueve casos del grupo con integración real contra H2. Las pruebas que escriben crean sus propios propietarios y revierten los cambios con `@Transactional`, sin modificar los datos semilla.
- `src/test/java/com/tecsup/petclinic/webs/OwnerControllerMockitoTest.java`: los mismos nueve casos con `@MockitoBean OwnerService`, organizados por integrante. Verifica el contrato HTTP/JSON y las llamadas al servicio, no la persistencia real.
- `src/test/java/com/tecsup/petclinic/utils/OwnerObjectCreator.java`: construye DTO nuevos en cada llamada para reutilizar datos sin compartir objetos mutables entre pruebas.

Los bloques A/B comprueban el listado, las consultas `200`/`404`, la creación `201` con todos los campos y el filtro por los dos propietarios Davis. Con Mockito también se verifica que el cuerpo sin `lastName` devuelve `400` sin llamar al servicio.

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

Validación del 5 de noviembre de 2026: **45 pruebas, 0 fallos, 0 errores y 0 omitidas** en la suite completa; 18 corresponden a los controladores de propietarios (9 H2 y 9 Mockito, 6 por integrante).

### Integración y conflictos

El trabajo de A y B llegó a `leonel` desde `amore` mediante fast-forward. No se presentaron conflictos durante este aporte de C; no se declara ninguna resolución de conflictos que no haya ocurrido.

Pendientes del equipo: registrar una resolución real de conflictos para cumplir la consigna e integrar los aportes a `main` con revisión.
