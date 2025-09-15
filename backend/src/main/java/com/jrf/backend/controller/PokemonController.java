package com.jrf.backend.controller;

import com.jrf.backend.model.Pokemon;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.jrf.backend.service.PokemonService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/pokemon")
public class PokemonController {

    private final PokemonService pokemonService;

    public PokemonController(PokemonService pokemonService) {
        this.pokemonService = pokemonService;
    }

    @GetMapping("/")
    public ResponseEntity<?> getAllPokemon(){
        List<Pokemon> response = this.pokemonService.getAllPokemon();

        if (response.isEmpty())
        {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        else
        {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPokemonById(@PathVariable int id){
        if (id <= 0)
        {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Id must be greater than zero"));
        }

        Pokemon response = this.pokemonService.getPokemonById(id);

        if (response == null)
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Invalid id: %d".formatted(id)));
        }

        return ResponseEntity.ok(response);
    }
}
