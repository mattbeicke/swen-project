import { Component, Input } from '@angular/core';


@Component({
  selector: 'app-account',
  standalone: false,
  templateUrl: './account.html',
  styleUrl: './account.css'
})
export class Account {
  @Input() 
  changeUsername(name:string){
    if (localStorage.getItem("role") != "manager") {
      localStorage.setItem("username", name)
      
    }
  }
}
