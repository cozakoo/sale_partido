import { Component, signal, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

interface HelloWorld {
  uuid: string;
  text: string;
  source: string;
}

@Component({
  standalone: true,
  selector: 'app-root',
  imports: [RouterOutlet, FormsModule],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App implements OnInit {
  protected readonly title = signal('frontend');
  textInput = '';
  helloWorlds: HelloWorld[] = [];

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.fetchHelloWorlds();
  }

  fetchHelloWorlds() {
    this.http.get<HelloWorld[]>('http://localhost:32328/').subscribe((data) => {
      this.helloWorlds = data;
    });
  }

  submitText() {
    if (!this.textInput.trim()) return;
    this.http.post('http://localhost:32328/', { text: this.textInput }).subscribe(() => {
      this.textInput = '';
      this.fetchHelloWorlds();
    });
  }
}
