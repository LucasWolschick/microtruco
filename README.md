# Microtruco

Sistema de jogos de truco online construído com Spring, microsserviços e RabbitMQ.

## Executando

Primeiro, certifique-se que versões recentes do Java (23), Maven e RabbitMQ estejam instaladas
em seu computador.

Abra um terminal nas pastas `games/`, `lobbies/` e `users/` e execute o comando:

```sh
$ mvn spring-boot:run
```

Em cada um deles.

Em seguida, acesse o navegador nos endereços:

- `http://localhost:8080` para o serviço de usuários;
- `http://localhost:8081` para o serviço de salas;
- `http://localhost:8082` para o serviço de jogos;
