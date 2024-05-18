import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';

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

  ngOnInit(): void {
    this.moveFish();
  }

  moveFish(): void {
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
