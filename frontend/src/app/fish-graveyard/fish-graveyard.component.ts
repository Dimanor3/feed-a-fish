import { AfterViewInit, Component, ElementRef, ViewChild } from '@angular/core';
import { FishService } from '../shared/fish.service';
import { Subscription } from 'rxjs';
import { ActivatedRoute } from '@angular/router';
import { FishStatus } from '../shared/fish-status.model';

export const FIELD_OF_VIEW = 500;

class Point3d {
  constructor(
    x: number,
    y: number,
    z: number,
    camera: { x: number; z: number; y: number }
  ) {
    z = z / FIELD_OF_VIEW;
    this.x = (x - camera.x) / (z + 1) + camera.x;
    this.y = (y - camera.y) / (z + 1) + camera.y;
  }
  x: number;
  y: number;
}

class Boid {
  constructor(
    name: String = '',
    color: string = '',
    xmax: number = 100,
    ymax: number = 100,
    zmax: number = 100,
    vmax: number = 10
  ) {
    this.name = name;
    this.color = color;
    this.x = Math.random() * xmax;
    this.y = Math.random() * ymax;
    this.z = Math.random() * zmax;
    this.vx = Math.random() * vmax * 2 - vmax;
    this.vy = Math.random() * vmax * 2 - vmax;
    this.vz = Math.random() * vmax * 2 - vmax;
  }
  name: String;
  color: string;
  x: number;
  y: number;
  z: number;
  vx: number;
  vy: number;
  vz: number;
  bin: {
    x: number;
    y: number;
    z: number;
  } = { x: 0, y: 0, z: 0 };
}

@Component({
  selector: 'app-fish-graveyard',
  standalone: true,
  imports: [],
  templateUrl: './fish-graveyard.component.html',
  styleUrl: './fish-graveyard.component.css',
})
export class FishGraveyardComponent implements AfterViewInit {
  deadFish: Subscription = null as any;

  @ViewChild('canvas')
  canvas!: ElementRef<HTMLCanvasElement>;
  ctx!: CanvasRenderingContext2D;

  depth: number = 1000;
  camera: { x: number; y: number; z: number } = { x: 0, y: 0, z: -1 };

  //parameters these can be tuned maybe even create sliders for them if you want
  size: number = 10; //how big each fish soul is

  avoidanceRate: number = 0.15; //how much fish souls want to have their social distance
  cohesionRate: number = 0.0015; //how much fish souls want to socialize
  alignmentRate: number = 0.15; //how much fish souls want to engage in herd behavior

  visionRange: number = 50; //range in which fish souls will socialize and engage in herd behavior
  protDist: number = 10; //range in which fish souls will social distance

  marginDist: number = 50; //range from border where fish souls will start to try to turn around

  minSpd: number = 2; //slowest a fish soul will go
  maxSpd: number = 3; //fastest a fish soul will go
  maxAcc: number = 2; //fastest a fish soul can change their speed
  turnRate: number = 0.2; //how fast fish souls will turn away from border

  //calculated parameters
  visionSquared: number = this.visionRange * this.visionRange;
  protDistSquared: number = this.protDist * this.protDist;
  rightMargin: number = 0;
  bottommargin: number = 0;
  leftMargin: number = this.marginDist;
  topmargin: number = this.marginDist;

  fish: FishStatus[] = [];
  boids: Boid[] = [];
  boidBins: Array<Array<Array<Array<Boid>>>> = [];

  constructor(private activatedRoute: ActivatedRoute) {
    this.activatedRoute.data.subscribe(({ deadFish }) => {
      this.fish = deadFish;
    });
  }

  draw() {
    //clear animation frame
    this.ctx.clearRect(
      0,
      0,
      this.canvas.nativeElement.width,
      this.canvas.nativeElement.height
    );

    //drawFrame
    let point = new Point3d(0, 0, 0, this.camera);
    this.ctx.beginPath();
    this.ctx.moveTo(point.x, point.y);
    point = new Point3d(this.canvas.nativeElement.width, 0, 0, this.camera);
    this.ctx.lineTo(point.x, point.y);
    point = new Point3d(
      this.canvas.nativeElement.width,
      this.canvas.nativeElement.height,
      0,
      this.camera
    );
    this.ctx.lineTo(point.x, point.y);
    point = new Point3d(
      this.canvas.nativeElement.width,
      this.canvas.nativeElement.height,
      this.depth,
      this.camera
    );
    console.log(point.x, point.y);
    this.ctx.lineTo(point.x, point.y);
    point = new Point3d(
      0,
      this.canvas.nativeElement.height,
      this.depth,
      this.camera
    );
    this.ctx.lineTo(point.x, point.y);
    point = new Point3d(0, this.canvas.nativeElement.height, 0, this.camera);
    this.ctx.lineTo(point.x, point.y);
    point = new Point3d(0, 0, 0, this.camera);
    this.ctx.lineTo(point.x, point.y);
    point = new Point3d(0, 0, this.depth, this.camera);
    this.ctx.lineTo(point.x, point.y);
    point = new Point3d(
      this.canvas.nativeElement.width,
      0,
      this.depth,
      this.camera
    );
    this.ctx.lineTo(point.x, point.y);
    point = new Point3d(
      this.canvas.nativeElement.width,
      this.canvas.nativeElement.height,
      this.depth,
      this.camera
    );
    this.ctx.lineTo(point.x, point.y);
    this.ctx.stroke();

    //update boids
    this.updateBoids();

    //draw boids
    this.drawBoids();
    //start next frame
    window.requestAnimationFrame(this.draw.bind(this));
  }

  ngAfterViewInit(): void {
    const context = this.canvas.nativeElement.getContext('2d');
    this.fish.forEach((fishStatus) => {
      this.boids.push(
        new Boid(
          fishStatus.name,
          `rgb(${Math.random() * 255} ${Math.random() * 255} ${
            Math.random() * 255
          } `,
          this.canvas.nativeElement.width,
          this.canvas.nativeElement.height,
          this.depth,
          this.maxSpd
        )
      );
    });
    this.bottommargin = this.canvas.nativeElement.height - this.marginDist;
    this.rightMargin = this.canvas.nativeElement.width - this.marginDist;
    this.camera.x = this.canvas.nativeElement.width / 2;
    this.camera.y = this.canvas.nativeElement.height / 2;

    if (context != null) {
      this.ctx = context;
      this.ctx.fillStyle = 'rgb(255 255 255 / 70%)';
      this.ctx.strokeStyle = 'rgb(255 255 255 / 100%)';
    }
    window.requestAnimationFrame(this.draw.bind(this));
  }

  updateBoids() {
    this.updateBins();
    this.boids.forEach((boid) => {
      const vx_prev = boid.vx;
      const vy_prev = boid.vy;
      const vz_prev = boid.vz;

      const neighbors = this.getNeighbors(boid);
      //seperation
      this.seperation(boid, neighbors.protected);

      //cohesion
      this.cohesion(boid, neighbors.vision);

      //alignment
      this.alignment(boid, neighbors.vision);

      //avoidance

      //limit acceleration
      const AccX = boid.vx - vx_prev;
      const AccY = boid.vy - vy_prev;
      const AccZ = boid.vz - vz_prev;

      const Acc = Math.sqrt(AccX * AccX + AccY * AccY + AccZ * AccZ);

      if (Acc > this.maxAcc) {
        boid.vx = (AccX / Acc) * this.maxAcc + vx_prev;
        boid.vy = (AccY / Acc) * this.maxAcc + vy_prev;
        boid.vz = (AccZ / Acc) * this.maxAcc + vz_prev;
      }

      this.avoidance(boid);

      //limit speed
      this.limitSpeed(boid);

      boid.x += boid.vx;
      boid.y += boid.vy;
      boid.z += boid.vz;
    });
  }

  draw3dPoint(x: number, y: number, z: number) {
    return;
  }

  drawBoids() {
    this.boids.forEach((boid) => {
      this.ctx.beginPath();
      const point = new Point3d(boid.x, boid.y, boid.z, this.camera);

      this.ctx.fillStyle = `${boid.color} / ${(this.depth / boid.z) * 50}%)`;
      this.ctx.fillText(boid.name.valueOf(), point.x, point.y - 10);
      this.ctx.arc(
        point.x,
        point.y,
        Math.abs(this.size / (boid.z / FIELD_OF_VIEW + 1)),
        0,
        2 * Math.PI
      );
      this.ctx.fill();
    });
  }

  updateBins() {
    this.boidBins = [];
    this.boids.forEach((boid) => {
      let x = Math.floor(boid.x / this.visionRange);
      let y = Math.floor(boid.y / this.visionRange);
      let z = Math.floor(boid.z / this.visionRange);
      if (this.boidBins[x] == undefined) {
        this.boidBins[x] = [];
      }
      if (this.boidBins[x][y] == undefined) {
        this.boidBins[x][y] = [];
      }
      if (this.boidBins[x][y][z] == undefined) {
        this.boidBins[x][y][z] = [];
      }
      this.boidBins[x][y][z].push(boid);
      boid.bin.x = x;
      boid.bin.y = y;
      boid.bin.z = z;
    });
  }

  getNeighbors(boid: Boid) {
    const neighbors: { protected: Boid[]; vision: Boid[] } = {
      protected: [],
      vision: [],
    };
    for (let i = boid.bin.x - 1; i < boid.bin.x + 1; i++) {
      for (let j = boid.bin.y - 1; j < boid.bin.y + 1; j++) {
        for (let k = boid.bin.z - 1; k < boid.bin.z + 1; k++) {
          if (
            this.boidBins[i] &&
            this.boidBins[i][j] &&
            this.boidBins[i][j][k]
          ) {
            this.boidBins[i][j][k].forEach((potNeighbor) => {
              const dx = boid.x - potNeighbor.x;
              const dy = boid.y - potNeighbor.y;
              const dz = boid.z - potNeighbor.z;
              const sqDist = dx * dx + dy * dy + dz * dz;
              if (sqDist < this.protDistSquared) {
                neighbors.protected.push(potNeighbor);
              } else if (sqDist < this.visionSquared) {
                neighbors.vision.push(potNeighbor);
              }
            });
          }
        }
      }
    }
    return neighbors;
  }

  seperation(boid: Boid, neighbors: Boid[]) {
    let close_dx = 0;
    let close_dy = 0;
    let close_dz = 0;
    neighbors.forEach((neighbor) => {
      close_dx += boid.x - neighbor.x;
      close_dy += boid.y - neighbor.y;
      close_dz += boid.z - neighbor.z;
    });
    boid.vx += close_dx * this.avoidanceRate;
    boid.vy += close_dy * this.avoidanceRate;
    boid.vz += close_dz * this.avoidanceRate;
  }

  cohesion(boid: Boid, neighbors: Boid[]) {
    if (neighbors.length) {
      let avg_x = 0;
      let avg_y = 0;
      let avg_z = 0;

      neighbors.forEach((neighbor) => {
        avg_x += neighbor.x;
        avg_y += neighbor.y;
        avg_z += neighbor.z;
      });

      avg_x /= neighbors.length;
      avg_y /= neighbors.length;
      avg_z /= neighbors.length;

      boid.vx += (avg_x - boid.x) * this.cohesionRate;
      boid.vy += (avg_y - boid.y) * this.cohesionRate;
      boid.vz += (avg_z - boid.z) * this.cohesionRate;
    }
  }

  alignment(boid: Boid, neighbors: Boid[]) {
    if (neighbors.length) {
      let avg_x = 0;
      let avg_y = 0;
      let avg_z = 0;

      neighbors.forEach((neighbor) => {
        avg_x += neighbor.vx;
        avg_y += neighbor.vy;
        avg_z += neighbor.vz;
      });

      avg_x /= neighbors.length;
      avg_y /= neighbors.length;
      avg_z /= neighbors.length;

      boid.vx += (avg_x - boid.vx) * this.alignmentRate;
      boid.vy += (avg_y - boid.vy) * this.alignmentRate;
      boid.vz += (avg_z - boid.vz) * this.alignmentRate;
    }
  }

  avoidance(boid: Boid) {
    if (boid.x < this.leftMargin) boid.vx += this.turnRate;
    if (boid.x > this.rightMargin) boid.vx -= this.turnRate;

    if (boid.y < this.topmargin) boid.vy += this.turnRate;
    if (boid.y > this.bottommargin) boid.vy -= this.turnRate;

    if (boid.z < this.marginDist) boid.vz += this.turnRate;
    if (boid.z > this.depth - this.marginDist) boid.vz -= this.turnRate;
  }

  limitSpeed(boid: Boid) {
    const speed = Math.sqrt(
      boid.vx * boid.vx + boid.vy * boid.vy + boid.vz * boid.vz
    );
    if (speed < this.minSpd) {
      boid.vx = (boid.vx / speed) * this.minSpd;
      boid.vy = (boid.vy / speed) * this.minSpd;
      boid.vz = (boid.vz / speed) * this.minSpd;
    }
    if (speed > this.maxSpd) {
      boid.vx = (boid.vx / speed) * this.maxSpd;
      boid.vy = (boid.vy / speed) * this.maxSpd;
      boid.vz = (boid.vz / speed) * this.maxSpd;
    }
  }
}
