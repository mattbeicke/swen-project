import { Component, Input } from '@angular/core';
import { AccountsService } from '../accountservice'
import { Router } from '@angular/router';

@Component({
  selector: 'app-forgotpassword',
  standalone: false,
  templateUrl: './forgotpassword.html',
  styleUrl: './forgotpassword.css'
})
export class ForgotPassword {
  constructor(private accountsService: AccountsService, private router: Router) { }

  username = "";
  question = "";

  @Input() response?: string;
  @Input() password?: string;

  passfield = false;

  verify() {
    this.accountsService.verifyUser(this.username, this.response!).subscribe({
      next: _ => {
        this.passfield = true;
      },
      error: error => {
        switch (error.status) {
          case 401:
            alert("Wrong answer");
            break;
          case 404:
            alert("User with that username does not exist!");
            this.router.navigate(['/login']);
            break;
          case 500:
            alert("Internal server error");
            break;
          default:
            alert("Unknown error, is server online?");
        }
      }
    })
  }

  reset() {
    this.password = this.password?.trim();
    if (!this.password) {
      alert("Missing password");
      return;
    }
    this.accountsService.resetPassword(this.username, this.password).subscribe({
      next: _ => {
        alert("Password changed successfully");
        this.router.navigate(['/login']);
      },
      error: error => {
        switch (error.status) {
          case 409:
            alert("User does not exist");
            this.router.navigate(['/login']);
            break;
          case 404:
            alert("User with that username does not exist!");
            this.router.navigate(['/login']);
            break;
          case 500:
            alert("Internal server error");
            break;
          default:
            alert("Unknown error, is server online?");
        }
      }
    });
  }

  ngOnInit() {
    this.question = localStorage.getItem("question")!;
    this.username = localStorage.getItem("username")!;
    if (this.question == "") {
      this.router.navigate(['/login']);
    }
    localStorage.setItem("question", "");
    localStorage.setItem("username", "");
  }
}
