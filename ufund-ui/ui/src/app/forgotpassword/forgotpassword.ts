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

  answer = "";
  username = "";
  @Input() response?: string;
  @Input() password?: string;

  passfield = false;

  verify() {
    if (this.answer == this.response) {
      this.passfield = true;
    } else {
      alert("Incorrect");
    }
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
          //PASSWORD FAILED TO BE UPDATED
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
    let question = localStorage.getItem("question")!;
    this.answer = localStorage.getItem("answer")!;
    this.username = localStorage.getItem("username")!;
    if (question == "" || this.answer == "") {
      this.router.navigate(['/login']);
    }
    localStorage.setItem("question", "");
    localStorage.setItem("answer", "");
    localStorage.setItem("username", "");
  }
}
