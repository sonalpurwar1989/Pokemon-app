package com.techelevator.dao;

import com.techelevator.models.PokemonDetail;

import java.util.List;

public interface PokemonDao {
    // abstract methods -- no bodies, only the header
    // CRUD!!!

    // create method
    PokemonDetail saveFavorites(PokemonDetail detail, int userId);

    // read
    List<PokemonDetail> getAllFavorites(int userId);

}
