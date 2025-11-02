import { Component } from '@angular/core';

@Component({
  selector: 'app-sidebar',
  standalone: false,
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.css'
})
export class Sidebar {
  isManager?: boolean;
  ngOnInit(): void {
    if (localStorage.getItem('role') == 'manager') {
      this.isManager = true;
    } else {
      this.isManager = false;
    }
  }

}
