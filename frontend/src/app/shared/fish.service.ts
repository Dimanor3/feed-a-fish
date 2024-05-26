import {
  HttpClient,
  HttpErrorResponse,
  HttpParams,
} from '@angular/common/http';
import { Injectable, OnDestroy } from '@angular/core';
import { catchError, tap } from 'rxjs/operators';
import { Subscription, Subject, throwError, Observable } from 'rxjs';
import { FishStatus } from './fish-status.model';

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
  private fishImageSub: Subscription = null as any;
  private fishImage = new Subject<String>();
  fishChanged = new Subject<FishStatus>();
  fishKilled = new Subject<FishStatus>();

  constructor(private http: HttpClient) {}

  public getFish() {
    const now = new Date();
    const hours = now.getHours();
    const minutes = now.getMinutes();

    let params = new HttpParams();
    params = params.append('hours', hours);
    params = params.append('minutes', minutes);

    // if (hours === 11 && minutes === 11) {
    if (true) {
      this.getFishSub = this.http
        .get(this.url[0] + this.url[1], {
          responseType: 'text',
          params: params,
        })
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

          const res: FishStatus = JSON.parse(response);

          const createdAt: Date = new Date(res.createdAt);

          this.fishChanged.next(
            new FishStatus(
              res.id,
              res.name,
              createdAt,
              res.parentFishId,
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
    const now = new Date();
    const hours = now.getHours();
    const minutes = now.getMinutes();

    let params = new HttpParams();
    params = params.append('hours', hours);
    params = params.append('minutes', minutes);

    this.feedFishSub = this.http
      .get(this.url[0] + this.url[3], {
        responseType: 'text',
        params: params
      })
      .pipe(
        catchError(this.handleError),
        tap((resData) => {
          console.log(resData);
        })
      )
      .subscribe((response) => {
        if (response === 'dead') {
          this.fishKilled.next(null as any);
          return;
        }

        this.getFish();
      });
  }

  public getFishImage(name: String, id: String): Observable<String> {
    this.fishImageSub = this.http
            .get("https://sharpfish.billkarnavas.com/" + name + "-" + id, { responseType: 'text' })
            .pipe(catchError(this.handleError),
              tap((resData) => {
                console.log(resData);
              })
            )
            .subscribe((res) => {
              console.log(res);
              console.log("String: " + res);
              
              
              this.fishImage.next(res.toString());
            });

    return this.fishImage.asObservable();
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
    this.fishImageSub.unsubscribe();
  }
}
