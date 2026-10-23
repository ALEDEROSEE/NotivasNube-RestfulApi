# NotivasNube · CRUD de las notas de NOTIVas contra restful-api.dev (GLAB-S10 BPAREJA)

Ejercicio de desarrollo del laboratorio 10. NOTIVas (promedios de cursos) guarda sus notas en Room; aquí la misma tabla `nota` (curso, evaluación, peso, valor) se crea, lee, edita y elimina en un servicio online, **https://api.restful-api.dev/objects**, con Retrofit + Gson.

Programación en Móviles · Tecsup · Benjamin Pareja · 2026-01.

- Arquitectura: `RestfulApi` (Retrofit) → `RemoteNotaRepository` → `NotasViewModel` (`StateFlow`) → Compose. Inyección de dependencias a mano en `AppContainer`.
- `GET /objects` solo lista los 13 objetos de fábrica del servicio, nunca los creados por uno. La app recuerda en `SharedPreferences` los ids que crea y los pide con `?id=A&id=B`.
- Los objetos de fábrica (ids 1–13) no se pueden editar ni borrar (el servidor responde 405).
- Pruebas JVM: `./gradlew testDebugUnitTest` (10 pruebas con un `RestfulApi` falso en memoria).

Video: `demo/GLAB-S10-NotivasNube-demo.mp4`.
