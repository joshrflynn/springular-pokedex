import { Component } from '@angular/core';
import { PokemonCard } from '../pokemon-card/pokemon-card';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { finalize } from 'rxjs';
import { Pokemon } from '../../types/pokemon';

@Component({
  selector: 'app-home',
  imports: [CommonModule, PokemonCard],
  templateUrl: './home.html',
  styleUrl: './home.scss',
})
export class Home {
  url: string = '/api/v1/pokemon/';
  loading: boolean = false;
  pokemonList: Pokemon[] = [];

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.loading = true;
    this.http
      .get(this.url)
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: (res) => (this.pokemonList = res as Pokemon[]),
        error: (err) => {},
      });
  }
}
