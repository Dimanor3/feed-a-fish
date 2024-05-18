import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Subscription, fromEvent } from 'rxjs';
import { throttleTime, map } from 'rxjs/operators';

@Component({
  selector: 'app-fish-tank',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './fish-tank.component.html',
  styleUrl: './fish-tank.component.css',
})
export class FishTankComponent implements OnInit {
  fishStyle: any = {
    position: 'absolute',
    transform: 'rotate(0deg)',
    left: '0px',
    top: '0px',
    transition: 'transform 2s, left 2s, top 2s',
  };
  mousePosX = -1;
  mousePosY = -1;

  private mousePosSubscription: Subscription = null as any;

  ngOnInit(): void {
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
      // this.moveFishTo(randomX, randomY);

      fish.style.left = this.mousePosX + 'px';
      fish.style.top = this.mousePosY + 'px';
    }
  }

  moveFishInterval(): void {
    setInterval(() => {
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
}
