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
  private readonly BACKEND_URL = 'http://138.36.96.63:32328';

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.fetchHelloWorlds();
  }

  fetchHelloWorlds() {
    this.http.get<HelloWorld[]>(`${this.BACKEND_URL}/`).subscribe((data) => {
      this.helloWorlds = data;
    });
  }

  submitText() {
    if (!this.textInput.trim()) return;
    this.http.post(`${this.BACKEND_URL}/`, { text: this.textInput }).subscribe(() => {
      this.textInput = '';
      this.fetchHelloWorlds();
    });
  }
}
