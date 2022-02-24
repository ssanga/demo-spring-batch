package com.example.demospringbatch.services;

import com.example.demospringbatch.model.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class MoviesService {
    @Autowired
    RestTemplate restTemplate;

    public List<Movie> getMovies(){

        String url="https://movies-rest-api.azurewebsites.net/Movies/";

        ResponseEntity<Movie[]> response =
                restTemplate.getForEntity(
                        url,
                        Movie[].class);

        Movie[] movies = response.getBody();
        List<Movie> m = Arrays.asList(movies);
        return  m;
    }
}
