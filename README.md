# Spring Security JWT

Este projeto foi desenvolvido para ilustrar como implementar autenticação e autorização utilizando Spring Security e JWT. A aplicação demonstra como configurar e proteger endpoints com tokens JWT, utilizando Spring Boot e várias outras tecnologias do ecossistema Spring.

## Tecnologias Utilizadas

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Spring MVC](https://docs.spring.io/spring-framework/reference/web/webmvc.html)
- [Spring Security](https://spring.io/projects/spring-security)
- [Spring Data JDBC](https://spring.io/projects/spring-data-jdbc)
- [H2 Database](https://www.h2database.com)

## Estrutura do Projeto

A aplicação está organizada em diversos pacotes, cada um responsável por uma parte específica da funcionalidade:

- **config**: Contém as classes de configuração do Spring, incluindo a configuração de segurança JWT.
- **controller**: Contém os controladores REST que gerenciam as requisições HTTP.
- **service**: Contém as classes de serviço que implementam a lógica de negócio.
- **repository**: Contém as interfaces de repositório que gerenciam a persistência dos dados.
- **model**: Contém as classes de modelo que representam as entidades do aplicativo.

## Configuração de Segurança

A configuração de segurança é feita na classe `SecurityConfig`, que define os beans necessários para a codificação e decodificação dos tokens JWT, além de configurar os filtros de segurança. Utilizamos uma chave pública e uma chave privada para garantir a segurança e a validade dos tokens JWT. As chaves são do tipo RSA (Rivest-Shamir-Adleman), um dos mais robustos algoritmos de criptografia assimétrica utilizados em segurança de dados.

## Geração das Chaves RSA

Para gerar suas chaves pública e privada, utilize os comandos abaixo com o OpenSSL:

1. **Gerar a chave privada:**

    ```sh
    openssl genpkey -algorithm RSA -out chave_privada.pem
    ```

2. **Gerar a chave pública:**

    ```sh
    openssl rsa -pubout -in chave_privada.pem -out chave_publica.pem
    ```

Estas chaves serão usadas para assinar e verificar os tokens JWT, garantindo sua integridade e autenticidade.

### Código Exemplo: SecurityConfig.java
```java
package br.com.wagnerpires.springsecurityjwt.config;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
  @Value("${jwt.public.key}")
  private RSAPublicKey key;
  @Value("${jwt.private.key}")
  private RSAPrivateKey priv;

  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable()) //* TODO: csrf desabilitado pq não faz muito sentido usá-lo com JWT. É mais utilizado para operações com cookies
        .authorizeHttpRequests(
            auth -> auth
                .requestMatchers("/authenticate").permitAll()
                .anyRequest().authenticated())
        .httpBasic(Customizer.withDefaults())
        .oauth2ResourceServer(
            conf -> conf.jwt(jwt -> jwt.decoder(jwtDecoder())));
    return http.build();
  }

  @Bean
  PasswordEncoder passwordEncoder() { // Encriptar a senha
    return new BCryptPasswordEncoder();
  }

  @Bean
  JwtDecoder jwtDecoder() {
    return NimbusJwtDecoder.withPublicKey(this.key).build();
  }

  @Bean
  JwtEncoder jwtEncoder() {
    JWK jwk = new RSAKey.Builder(this.key).privateKey(this.priv).build(); // JSON Web Key
    JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
    return new NimbusJwtEncoder(jwks);
  }
}
```

## Como Executar

Para executar a aplicação em sua máquina local, siga os passos abaixo:

1. **Clonar o repositório git:**

    ```sh
    git clone https://github.com/wagnerdba/spring-security-04.git
    ```

2. **Construir o projeto:**

    ```sh
    ./mvnw clean package
    ```

3. **Executar o jar gerado:**

    ```sh
    java -jar ./target/spring-security-jwt-0.0.1-SNAPSHOT.jar
    ```

4. **Testar a aplicação utilizando [httppie](https://httpie.io):**

   Autenticar:
    ```sh
    http -a username:password POST :8080/authenticate
    ```

   Usar o token JWT retornado para acessar um endpoint protegido:
    ```sh
    JWT=<token>
    http :8080/private -A bearer -a ${JWT}
    ```

## Exemplo de Uso com curl

1. **Autenticar utilizando o endpoint de autenticação:**

```sh
curl -X POST -u username:password http://localhost:8080/authenticate
```

**Resposta esperada:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

2. **Acessar um endpoint protegido com o token JWT retornado:**

```sh
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/private
```

## Considerações de Segurança

- **JWT (JSON Web Tokens)**: Utilizamos a combinação de uma chave privada e uma chave pública do tipo RSA para garantir a assinatura e a integridade dos tokens JWT. Isso assegura que os tokens não possam ser alterados sem detecção.

- **Criptografia de Senhas**: As senhas dos usuários são armazenadas no banco de dados em formato criptografado utilizando o algoritmo BCrypt. Isso adiciona uma camada adicional de segurança, dificultando a recuperação das senhas originais a partir dos valores armazenados.

## Licença

Este projeto é licenciado sob os termos da licença MIT.

---

Se você encontrou este projeto útil, por favor, considere dar uma estrela no GitHub. Sugestões e contribuições são bem-vindas!