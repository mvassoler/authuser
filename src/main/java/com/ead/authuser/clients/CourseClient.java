package com.ead.authuser.clients;

import com.ead.authuser.dtos.CourseDto;
import com.ead.authuser.dtos.ResponsePageDto;
import com.ead.authuser.service.UtilsService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Log4j2
@Component
public class CourseClient {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    UtilsService utilsService;

    @Value("${ead.api.url.course}")
    String REQUEST_URI;

    //O name do retry é o mesmo defenido no application-dev.yaml nas configurações do Resilience4j
    //Método fallback retryFallBack implementado abaixo
    //@Retry(name = "retryInstance", fallbackMethod = "retryFallBack")
    //O name do CircuitBreaker é o mesmo defenido no application-dev.yaml nas configurações do Resilience4j, mas no grupo circuitbreaker
    //Método fallback circuitBreakerFallBack implementado abaixo
    @CircuitBreaker(name = "circuitbreaker") //, fallbackMethod = "circuitBreakerFallBack")
    public Page<CourseDto> getAllCoursesByUser(UUID userId, Pageable pageable, String token){
        List<CourseDto> searchResult = null;
        String url = REQUEST_URI + this.utilsService.createUrl(userId, pageable);

        //Montando o header com o token
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        //Montando o entity que será passado ao RestTemplate
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);

        log.debug("Request URL: {}", url);
        log.info("Request URL: {}", url);

        ParameterizedTypeReference<ResponsePageDto<CourseDto>> responseType = new ParameterizedTypeReference<ResponsePageDto<CourseDto>>() {};
        ResponseEntity<ResponsePageDto<CourseDto>> result = restTemplate.exchange(url, HttpMethod.GET, entity, responseType);
        searchResult = result.getBody().getContent();
        log.debug("Response Number of Elements: {}", searchResult.size());

        log.info("Ending request /courses userId {}", userId);
        return new PageImpl<>(searchResult);
    }

    //Método de fallback para falha na comunição com o microservice course
    public Page<CourseDto> retryFallBack(UUID userId, Pageable pageable, Throwable t){
        log.info("Inside retry retryfallback, cause - {}", t.toString());
        List<CourseDto> searchResult = new ArrayList<>();
        return new PageImpl<>(searchResult);
    }

    //Método de fallback para falha na comunição com o microservice course
    public Page<CourseDto> circuitBreakerFallBack(UUID userId, Pageable pageable, Throwable t){
        log.info("Inside retry retryfallback, cause - {}", t.toString());
        List<CourseDto> searchResult = new ArrayList<>();
        return new PageImpl<>(searchResult);
    }

}
