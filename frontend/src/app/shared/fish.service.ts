import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, tap } from 'rxjs/operators';
import { throwError } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class FishService {
  private url: string[] = [
    'bijanrazavi.com/feed-a-fish/api/',
    'get/latest',
    'list/dead',
  ];

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

    return throwError(errorRes);
  }
}
