import { Component, signal, inject, OnInit } from '@angular/core';
import { FineApi } from '../fine-api';
import { Fine } from '../fine';


@Component({
  selector: 'app-fine-list',
  imports: [],
  templateUrl: './fine-list.html',
  styleUrl: './fine-list.css',
})
export class FineList implements OnInit{
  private fineApi = inject(FineApi);
  fines = signal<Fine[]>([]);

  ngOnInit() {
    this.fineApi.getFines().subscribe(data => {
      this.fines.set(data.content);
    });
  }
}
