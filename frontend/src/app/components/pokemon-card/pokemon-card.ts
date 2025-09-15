import { Component, Input } from '@angular/core';
import { Pokemon } from '../../types/pokemon';

@Component({
  selector: 'app-pokemon-card',
  imports: [],
  templateUrl: './pokemon-card.html',
  styleUrl: './pokemon-card.scss',
})
export class PokemonCard {
  @Input() pokemon!: Pokemon;
}
