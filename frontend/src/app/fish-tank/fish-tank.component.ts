import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription, fromEvent } from 'rxjs';
import { throttleTime, map } from 'rxjs/operators';
import { FishService } from '../shared/fish.service';
import { FishStatus } from '../shared/fish-status.model';

@Component({
  selector: 'app-fish-tank',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './fish-tank.component.html',
  styleUrl: './fish-tank.component.css',
})
export class FishTankComponent implements OnInit, OnDestroy {
  fishStyle: any = {
    position: 'absolute',
    transform: 'rotate(0deg)',
    left: '100px',
    top: '100px',
    transition: 'transform 2s, left 2s, top 2s',
  };
  fishSub: Subscription = null as any;
  fishKilledSub: Subscription = null as any;
  fish: FishStatus = null as any;
  fishWidth: String = '10%';
  fishDead: boolean = false;
  killedFish: boolean = false;
  canFeedFish: boolean = true;
  fishName: String = '';
  fishImagePath: String = '';

  fishInterval: Number = null as any;
  fishIntervalMovement: boolean = false;

  subscriptionsInitialized: boolean = false;

  private width: Number = 3;
  private minWidth: Number = 3;
  private maxWidth: Number = 43;

  mousePosX = -1;
  mousePosY = -1;

  private mousePosSubscription: Subscription = null as any;

  constructor(private fishService: FishService) {}

  ngOnInit(): void {
    this.fishService.getFish();

    this.fishSub = this.fishService.fishChanged.subscribe(
      (fish: FishStatus) => {
        if (fish === null) {
          this.fishDead = true;
        } else {
          this.fishDead = false;
          this.killedFish = false;
          this.fish = fish;
          this.fishName = fish.name;
          this.fishImagePath = fish.imagePath;
          this.updateWidth();
          this.fishWidth = this.width + '%';

          if (!this.subscriptionsInitialized) {
            this.subscriptionsInitialized = true;
            this.initalizeSubscriptions();
          }
        }
      }
    );

    const now = new Date();
    const hours = now.getHours();
    const minutes = now.getMinutes();

    if (hours === 11 && minutes === 11) {
      this.canFeedFish = true;
    }
  }

  initalizeSubscriptions() {
    this.fishKilledSub = this.fishService.fishKilled.subscribe(
      (fish: FishStatus) => {
        if (fish === null) {
          this.killedFish = true;
          this.fishDead = true;

          console.log('dead fish: ' + this.fishDead);
          console.log('killed fish: ' + this.killedFish);

          this.fish = null as any;

          this.fishSub.unsubscribe();
          this.fishKilledSub.unsubscribe();
          this.mousePosSubscription.unsubscribe();
          clearInterval(+this.fishInterval);
        }
      }
    );

    this.mousePosSubscription = fromEvent<MouseEvent>(document, 'mousemove')
      .pipe(
        throttleTime(100),
        map((event: MouseEvent) => ({ x: event.clientX, y: event.clientY }))
      )
      .subscribe((e) => {
        this.mousePosX = e.x;
        this.mousePosY = e.y;

        this.moveFish();
      });
  }

  moveFish(): void {
    const fish = document.getElementById('fish');

    if (fish) {
      if (
        +this.fish.currentHungerLevel >=
        +this.fish.gainWeightHungerLevel + 6
      ) {
        this.fishIntervalMovement = false;
        clearInterval(+this.fishInterval);

        // this.moveFishTo(randomX, randomY);

        fish.style.left = this.mousePosX - fish.offsetWidth / 2 + 'px';
        fish.style.top = this.mousePosY - fish.offsetHeight / 2 + 'px';
      } else {
        if (!this.fishIntervalMovement) {
          this.moveFishInterval();
          this.fishIntervalMovement = true;
        }
      }
    }
  }

  moveFishInterval(): void {
    this.fishInterval = window.setInterval(() => {
      const fish = document.getElementById('fish');
      if (fish) {
        // Check if 'fish' is not null
        const maxX = window.innerWidth - fish.offsetWidth;
        const maxY = window.innerHeight - fish.offsetHeight;

        const randomX = Math.random() * maxX;
        const randomY = Math.random() * maxY;

        // this.moveFishTo(randomX, randomY);

        fish.style.left = randomX + 'px';
        fish.style.top = randomY + 'px';
      }
    }, 2000);
  }

  moveFishTo(x: number, y: number): void {
    const fishElement = document.getElementById('fish');
    if (!fishElement) return;

    const rect = fishElement.getBoundingClientRect();
    const startX = rect.left + rect.width / 2;
    const startY = rect.top + rect.height / 2;

    const angle = Math.atan2(y - startY, x - startX);
    const degrees = (angle * 180) / Math.PI;

    this.fishStyle = {
      ...this.fishStyle,
      transform: `rotate(${degrees}deg)`,
      left: `${x - rect.width / 2}px`,
      top: `${y - rect.height / 2}px`,
    };
  }

  feedFish() {
    const now = new Date();
    const hours = now.getHours();
    const minutes = now.getMinutes();

    if (hours === 11 && minutes === 11) {
      this.canFeedFish = true;

      if (this.killedFish) {
        return;
      }

      this.fishService.feedFish();
      this.updateWidth();
      this.fishWidth = this.width + '%';
    } else {
      this.canFeedFish = false;
    }
  }

  updateWidth() {
    if (this.fish.weight <= this.fish.minWeight) {
      this.width = this.minWidth;
    } else if (this.fish.weight >= this.fish.maxWeight) {
      this.width = this.maxWidth;
    } else {
      this.width =
        +this.minWidth +
        ((+this.fish.weight - +this.fish.minWeight) *
          (+this.maxWidth - +this.minWidth)) /
          (+this.fish.maxWeight - +this.fish.minWeight);
    }
  }

  ngOnDestroy(): void {
    this.mousePosSubscription.unsubscribe();
    this.fishSub.unsubscribe();
    this.fishKilledSub.unsubscribe();
  }
}
