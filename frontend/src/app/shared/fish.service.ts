import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable, OnDestroy } from '@angular/core';
import { catchError, tap } from 'rxjs/operators';
import { Subscription, Subject, throwError } from 'rxjs';
import { FishStatus } from './fish-status.model';

export interface Fish {
  id: Number;
  name: String;
  createdAt: Date;
  parentFishId: Number;
  imagePath: String;
  mood: String;
  age: Number;
  alive: Boolean;
  weight: Number;
  minWeight: Number;
  maxWeight: Number;
  currentHungerLevel: Number;
  gainWeightHungerLevel: Number;
  loseWeightHungerLevel: Number;
}

@Injectable({ providedIn: 'root' })
export class FishService implements OnDestroy {
  private url: string[] = [
    'https://bijanrazavi.com/feed-a-fish/api/',
    'get/latest',
    'list/dead',
    'feed/latest',
  ];
  private getFishSub: Subscription = null as any;
  private getDeadSub: Subscription = null as any;
  private feedFishSub: Subscription = null as any;
  fishChanged = new Subject<FishStatus>();

  constructor(private http: HttpClient) {}

  public getFish() {
    const now = new Date();
    const hours = now.getHours();
    const minutes = now.getMinutes();

    console.log(this.url[0] + this.url[1]);

    // if (hours === 11 && minutes === 11) {
    if (true) {
      this.getFishSub = this.http
        .get(this.url[0] + this.url[1], { responseType: 'text' })
        .pipe(
          catchError(this.handleError),
          tap((resData) => {
            console.log(resData);
          })
        )
        .subscribe((response) => {
          if (response === 'dead') {
            this.fishChanged.next(null as any);
            return;
          }

          const res: Fish = JSON.parse(response);

          const createdAt: Date = new Date(res.createdAt);
          const imagePath: String = this.url[0] + res.imagePath;
          // console.log(imagePath);

          this.fishChanged.next(
            new FishStatus(
              res.id,
              res.name,
              createdAt,
              res.parentFishId,
              imagePath,
              res.mood,
              res.age,
              res.alive,
              res.weight,
              res.minWeight,
              res.maxWeight,
              res.currentHungerLevel,
              res.gainWeightHungerLevel,
              res.loseWeightHungerLevel
            )
          );
        });
    }
  }

  public getDeadFish() {
    this.getDeadSub = this.http
      .get(this.url[0] + this.url[2])
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

  public feedFish() {
    this.feedFishSub = this.http
      .get(this.url[0] + this.url[3], { responseType: 'text' })
      .pipe(
        catchError(this.handleError),
        tap((resData) => {
          console.log(resData);
        })
      )
      .subscribe((response) => {
        this.getFish();
      });
  }

  private handleError(errorRes: HttpErrorResponse) {
    // let errorMessage = "An unkown error occurred!";
    console.log(errorRes);

    if (!errorRes.error || !errorRes.error.error) {
      return throwError(errorRes);
    }

    return throwError(errorRes);
  }

  ngOnDestroy(): void {
    this.getFishSub.unsubscribe();
    this.getDeadSub.unsubscribe();
    this.feedFishSub.unsubscribe();
  }
}
