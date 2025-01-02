# Microtruco

Sistema de jogos de truco online construído com Spring, microsserviços, Eureka e RabbitMQ.

## Executando

Primeiro, certifique-se que versões recentes do Java (23), Maven e RabbitMQ estejam instaladas
em seu computador. Você também precisará do banco de dados PostgreSQL instalado para o microsserviço
`games`.

Primeiro, inicialize o servidor Eureka abrindo um terminal na pasta `discovery-service` e
execute o comando:

```sh
$ mvn spring-boot:run
```

Em seguida, faça o mesmo nas pastas `gateway-service/`, `lobbies/` e `users/`.

Para o serviço `games/`, é necessário configurar o banco de dados PostgreSQL. Crie um novo
banco de dados e insira a sua URL de conexão no arquivo `games/src/main/java/GamesApplication.java`,
além do usuário e senha necessários para acessar o banco de dados. Depois disso, execute o comando
listado acima, assim como foi feito nos demais serviços.

O único serviço que pode ser replicado é o `games/`. Crie várias instâncias do microsserviço e veja
como o balanceamento de carga e tolerância a falhas funcionam.

Acesse o navegador no endereço http://localhost:8761/ para ver os serviços registrados
no Eureka. A documentação de cada serviço pode ser encontrada acessando-se o seu socket no
navegador e navegando até a URL `/docs`. As APIs podem ser acessadas a partir da API Gateway, disponível no endereço http://localhost:8080/.
