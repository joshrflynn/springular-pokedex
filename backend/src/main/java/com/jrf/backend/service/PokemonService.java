package com.jrf.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jrf.backend.model.GraphPostRequestBody;
import com.jrf.backend.model.Pokemon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PokemonService {
    private static final Logger log = LoggerFactory.getLogger(PokemonService.class);

    private final RestTemplate restTemplate;

    private final String baseUrl = "https://graphql.pokeapi.co/v1beta2/";

    public PokemonService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Pokemon getPokemonById(int id){
        String graphQuery = """
                query ($id: Int!) {
                  pokemonspecies(where: {id: {_eq: $id}}, limit: 1) {
                    pokemonspeciesnames(where: {language_id: {_eq: 9}}, limit: 1) {
                      name
                    }
                  }
                }
                """;

        Map<String,Object> variables = Map.of("id",id);

        GraphPostRequestBody requestBody = new GraphPostRequestBody(graphQuery, variables, null);
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, requestBody, String.class);

        try
        {
            JsonNode nameNode = new ObjectMapper().readTree(response.getBody()).at("/data/pokemonspecies/0/pokemonspeciesnames/0/name");;
            if (nameNode.isMissingNode() || nameNode.isNull()) {
               return null;
            }
            return new Pokemon(id, nameNode.asText(), null);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to parse JSON", e);
        }
    }

    public List<Pokemon> getAllPokemon() {
        String graphQuery = """
                query {
                  root: pokemonspeciesname(where: {language_id: {_eq: 9}}) {
                    name
                    species: pokemonspecy {
                      pokemon: pokemons {
                        spriteList: pokemonsprites {
                          sprite: sprites(path: "other.official-artwork.front_default")
                        }
                      }
                      id
                    }
                  }
                }
                """;

        GraphPostRequestBody requestBody = new GraphPostRequestBody(graphQuery, null, null);
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl, requestBody, String.class);


        List<Pokemon> pokemonList = new ArrayList<>();
        try
        {
            JsonNode rootNode = new ObjectMapper().readTree(response.getBody()).at("/data/root");
            if (rootNode.isMissingNode() || rootNode.isNull() || !rootNode.isArray() || rootNode.isEmpty()) {
                return pokemonList;
            }

            for (int i = 0; i < rootNode.size(); i++) {
                JsonNode pokemonNode =  rootNode.get(i);
                String name = pokemonNode.get("name").asText();
                String sprite = pokemonNode.at("/species/pokemon/0/spriteList/0").get("sprite").asText();
                int id = pokemonNode.at("/species").get("id").asInt();

                pokemonList.add(new Pokemon(id, name, sprite));
            }


            return pokemonList;
        }
        catch (Exception e)
        {
            throw new RuntimeException("Failed to parse JSON", e);
        }
    }
}
