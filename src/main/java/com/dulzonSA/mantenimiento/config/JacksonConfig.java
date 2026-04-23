package com.dulzonSA.mantenimiento.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de Jackson para evitar errores de serialización con proxies Hibernate.
 *
 * PROBLEMA QUE RESUELVE:
 * "Type definition error: [simple type, class org.hibernate.proxy.pojo.bytebuddy.ByteBuddyInterceptor]"
 *
 * CAUSA:
 * Las entidades JPA con FetchType.LAZY generan proxies de Hibernate en tiempo de ejecución.
 * Jackson intenta serializar esos proxies y falla porque ByteBuddyInterceptor no tiene
 * mapeador JSON definido.
 *
 * SOLUCIÓN AQUÍ:
 * 1. Hibernate6Module instruye a Jackson para que serialice solo propiedades ya inicializadas.
 * 2. FORCE_LAZY_LOADING = false: no lanza excepción, devuelve null para las no cargadas.
 * 3. USE_TRANSIENT_ANNOTATION = true: respeta @Transient para excluir campos calculados.
 *
 * SOLUCIÓN PRINCIPAL (MantenimientoService + MantenimientoController):
 * Los endpoints de acción ahora retornan DTOs en lugar de entidades JPA.
 * Esta configuración es una capa de protección adicional para casos no previstos.
 */
@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // Módulo específico para Hibernate 6: maneja proxies y colecciones lazy
        Hibernate6Module hibernate6Module = new Hibernate6Module();
        hibernate6Module.configure(Hibernate6Module.Feature.FORCE_LAZY_LOADING, false);
        hibernate6Module.configure(Hibernate6Module.Feature.USE_TRANSIENT_ANNOTATION, true);
        hibernate6Module.configure(Hibernate6Module.Feature.SERIALIZE_IDENTIFIER_FOR_LAZY_NOT_LOADED_OBJECTS, true);

        // Módulo para fechas Java 8+ (LocalDate, LocalDateTime)
        JavaTimeModule javaTimeModule = new JavaTimeModule();

        mapper.registerModule(hibernate6Module);
        mapper.registerModule(javaTimeModule);

        // Serializar fechas como string ISO, no como timestamps numéricos
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // No fallar si el JSON tiene campos que no existen en el DTO
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        return mapper;
    }
}
