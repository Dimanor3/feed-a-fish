import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, tap } from 'rxjs/operators';
import { throwError } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class FishService {
  private url: string[] = ['localhost:8081/', 'get/latest', 'list/dead'];

  constructor(private http: HttpClient) {}

  public generateFish() {
    const now = new Date();
    const hours = now.getHours();
    const minutes = now.getMinutes();

    // if (hours === 11 && minutes === 11) {
    if (true) {
      this.http
        .get(this.url[0] + this.url[1])
        .pipe(
          catchError(this.handleError),
          tap((resData) => {
            console.log(resData);
          })
        )
        .subscribe((response) => {
          console.log(response);
        });
    }
  }

  private handleError(errorRes: HttpErrorResponse) {
    // let errorMessage = "An unkown error occurred!";
    console.log(errorRes);

    if (!errorRes.error || !errorRes.error.error) {
      return throwError(errorRes);
    }

    // switch(errorRes.error.error.message) {
    //     case 'EMAIL_EXISTS':
    //         errorMessage = "This error exists already!";
    //     case 'EMAIL_NOT_FOUND':
    //         errorMessage = "This email does not exist!";
    //     case 'INVALID_PASSWORD':
    //         errorMessage = "This password is not correct!";
    //     case "INVALID_LOGIN_CREDENTIALS":
    //         errorMessage = "Invalid login credentials!";
    // case 'Permission denied':
    //     errorMessage = "Permission denied!";
    // }

    return throwError(errorRes);
  }
}
