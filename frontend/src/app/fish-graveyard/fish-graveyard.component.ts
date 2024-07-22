import { AfterViewInit, Component, ElementRef, ViewChild } from '@angular/core';

class Boid {
  constructor(xmax: number = 100, ymax: number = 100, vmax: number = 10) {
    this.x = Math.random() * xmax
    this.y = Math.random() * ymax
    this.vx = (Math.random() * vmax * 2) - vmax
    this.vy = (Math.random() * vmax * 2) - vmax
  }
  x: number;
  y: number;
  vx: number;
  vy: number;
  bin: {
    x: number
    y: number
  } = {x: 0, y: 0}
}

@Component({
  selector: 'app-fish-graveyard',
  standalone: true,
  imports: [],
  templateUrl: './fish-graveyard.component.html',
  styleUrl: './fish-graveyard.component.css'
})

export class FishGraveyardComponent implements AfterViewInit{

  @ViewChild('canvas')
  canvas!: ElementRef<HTMLCanvasElement>;
  ctx!: CanvasRenderingContext2D;

  //parameters these can be tuned maybe even create sliders for them if you want
  size: number = 2  //how big each fish soul is

  avoidanceRate: number =  0.15;  //how much fish souls want to have their social distance
  cohesionRate: number =  0.0015; //how much fish souls want to socialize
  alignmentRate: number =  0.15; //how much fish souls want to engage in herd behavior

  visionRange: number = 20; //range in which fish souls will socialize and engage in herd behavior
  protDist: number = 2;    //range in which fish souls will social distance

  marginDist: number = 50; //range from border where fish souls will start to try to turn around

  minSpd: number = 5; //slowest a fish soul will go
  maxSpd: number = 10;  //fastest a fish soul will go
  turnRate: number = 1 //how fast fish souls will turn away from border

  //calculated parameters
  visionSquared: number = this.visionRange*this.visionRange;
  protDistSquared: number = this.protDist*this.protDist;
  rightMargin: number = 0
  bottommargin: number = 0;
  leftMargin: number = this.marginDist
  topmargin: number = this.marginDist;

  numSouls: number = 1000
  boids: Boid[] = [];
  boidBins: Array<Array<Array<Boid>>> = [];

  constructor(){

  }
  
  draw() {
    
    //clear animation frame
    this.ctx.clearRect(0, 0, this.canvas.nativeElement.width, this.canvas.nativeElement.height)

    //drawFrame
    this.ctx.beginPath()
    this.ctx.moveTo(0, 0)
    this.ctx.lineTo(0, this.canvas.nativeElement.height)
    this.ctx.lineTo(this.canvas.nativeElement.width, this.canvas.nativeElement.height)
    this.ctx.lineTo(this.canvas.nativeElement.width, 0)
    this.ctx.lineTo(0, 0)
    this.ctx.stroke()

    //update boids
    this.updateBoids()

    //draw boids
    this.drawBoids()
    //start next frame
    window.requestAnimationFrame(this.draw.bind(this))
  }

  ngAfterViewInit(): void {
    const context = this.canvas.nativeElement.getContext('2d')
    this.bottommargin = this.canvas.nativeElement.height - this.marginDist;
    this.rightMargin = this.canvas.nativeElement.width - this.marginDist;
    for(let i = 0; i < this.numSouls; i++) {
      this.boids.push(new Boid(this.canvas.nativeElement.width, this.canvas.nativeElement.height, this.maxSpd))
    }
    if(context != null){
      this.ctx = context
      this.ctx.fillStyle = "rgb(255 255 255 / 70%)";
      this.ctx.strokeStyle = "rgb(255 255 255 / 100%)";
    }
    window.requestAnimationFrame(this.draw.bind(this))
  }

  updateBoids() {
    this.updateBins()
    this.boids.forEach(boid => {


      const neighbors = this.getNeighbors(boid)
      //seperation
      this.seperation(boid, neighbors.protected)

      //cohesion
      this.cohesion(boid, neighbors.vision)
      
      //alignment
      this.alignment(boid, neighbors.vision)

      //avoidance
      this.avoidance(boid)

      //limit speed
      this.limitSpeed(boid)

      boid.x += boid.vx;
      boid.y += boid.vy;
    })
  }

  drawBoids() {
    this.boids.forEach(boid => {
      this.ctx.beginPath();
      this.ctx.arc(boid.x, boid.y, this.size, 0, 2 * Math.PI);
      this.ctx.fill();
    })
  }

  updateBins() {
    this.boidBins = [];
    this.boids.forEach(boid => {
      let x = Math.floor(boid.x / this.visionRange);
      let y = Math.floor(boid.y / this.visionRange);
      if(this.boidBins[x] == undefined) {
        this.boidBins[x] = []
      }
      if(this.boidBins[x][y] == undefined) {
        this.boidBins[x][y] = []

      }
      this.boidBins[x][y].push(boid)
      boid.bin.x = x
      boid.bin.y = y
    })
  }

  getNeighbors(boid: Boid) {
    const neighbors: { protected: Boid[]; vision: Boid[]; } = {protected: [], vision: []}
    for(let i = boid.bin.x - 1; i < boid.bin.x + 1; i++) {
      for(let j = boid.bin.y - 1; j < boid.bin.y + 1; j++) {
        if(this.boidBins[i] && this.boidBins[i][j]) {
          this.boidBins[i][j].forEach(potNeighbor => {
            const dx = boid.x - potNeighbor.x
            const dy = boid.y - potNeighbor.y
            const sqDist = (dx*dx) + (dy*dy)
            if(sqDist < this.protDistSquared) {
              neighbors.protected.push(potNeighbor)
            } else if (sqDist < this.visionSquared){
              neighbors.vision.push(potNeighbor)
            }
          })
        }
      }
    }
    return neighbors
  }

  seperation(boid: Boid, neighbors: Boid[]) {
    let close_dx = 0
    let close_dy = 0
    neighbors.forEach(neighbor => {
      close_dx += boid.x - neighbor.x
      close_dy += boid.y - neighbor.y
    }) 
    boid.vx += close_dx * this.avoidanceRate
    boid.vy += close_dy * this.avoidanceRate
  }

  cohesion(boid: Boid, neighbors: Boid[]) {
    if(neighbors.length) {
      let avg_x = 0
      let avg_y = 0
  
      neighbors.forEach(neighbor => {
        avg_x += neighbor.x
        avg_y += neighbor.y
      })

      avg_x /= neighbors.length
      avg_y /= neighbors.length

      boid.vx += (avg_x - boid.x) * this.cohesionRate
      boid.vy += (avg_y - boid.y) * this.cohesionRate
    }
  }

  alignment(boid: Boid, neighbors: Boid[]) {
    if(neighbors.length) {
      let avg_x = 0
      let avg_y = 0
  
      neighbors.forEach(neighbor => {
        avg_x += neighbor.vx
        avg_y += neighbor.vy
      })

      avg_x /= neighbors.length
      avg_y /= neighbors.length

      boid.vx += (avg_x - boid.vx) * this.alignmentRate
      boid.vy += (avg_y - boid.vy) * this.alignmentRate
    }
  }

  avoidance(boid: Boid) {
    if (boid.x < this.leftMargin)
      boid.vx += this.turnRate
    if (boid.x > this.rightMargin)
      boid.vx -= this.turnRate

    if (boid.y < this.topmargin)
      boid.vy += this.turnRate
    if (boid.y > this.bottommargin)
      boid.vy -= this.turnRate
  }

  limitSpeed(boid: Boid) {
    const speed = Math.sqrt(boid.vx*boid.vx + boid.vy*boid.vy)
    if(speed < this.minSpd) {
      boid.vx = (boid.vx/speed)*this.minSpd
      boid.vy = (boid.vy/speed)*this.minSpd
    }
    if(speed > this.maxSpd) {
      boid.vx = (boid.vx/speed)*this.maxSpd
      boid.vy = (boid.vy/speed)*this.maxSpd
    }
  }
}
