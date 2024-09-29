package com.techelevator.services;

import com.techelevator.models.Pokemon;
import com.techelevator.models.PokemonDetail;
import com.techelevator.models.Results;
import org.springframework.web.client.RestTemplate;

import java.util.List;

public class PokemonService {

    private RestTemplate restTemplate = new RestTemplate();

    private final String API_URL = "https://pokeapi.co/api/v2/pokemon/";

   public List<Pokemon> getPokemon() {
       Results rs = restTemplate.getForObject(API_URL, Results.class);


       return rs.getResults();
   }

    public PokemonDetail getPokemonDetailById(int id) {
        PokemonDetail pokemonDetail = restTemplate.getForObject(API_URL + id, PokemonDetail.class);
//        System.out.println(pokemonDetail);
        return pokemonDetail;
    }

    public List<Pokemon> getMorePokemon(int startVal) {
        Results rs =  restTemplate.getForObject(API_URL + "?offset=" + startVal + "&limit=20", Results.class);
        List<Pokemon> list = rs.getResults();
        for (Pokemon item: list){
            String url = item.getUrl();
            int pokemonIndex = url.indexOf("pokemon");
            String pokemonString = url.substring(pokemonIndex);
            int slashIndex = pokemonString.indexOf("/");
            String number = pokemonString.substring(slashIndex + 1, pokemonString.length() - 1);
            int id = Integer.parseInt(number);
            item.setId(id);
        }
        return list;
    }

}
