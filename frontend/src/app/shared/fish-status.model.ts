export class FishStatus {
  public id: Number;
  public name: String;
  public createdAt: Date;
  public parentFishId: Number;
  public imagePath: String;
  public mood: String;
  public age: Number;
  public alive: Boolean;
  public weight: Number;
  public minWeight: Number;
  public maxWeight: Number;
  public currentHungerLevel: Number;
  public gainWeightHungerLevel: Number;
  public loseWeightHungerLevel: Number;

  constructor(
    id: Number,
    name: String,
    createdAt: Date,
    parentFishId: Number,
    imagePath: String,
    mood: String,
    age: Number,
    alive: Boolean,
    weight: Number,
    minWeight: Number,
    maxWeight: Number,
    currentHungerLevel: Number,
    gainWeightHungerLevel: Number,
    loseWeightHungerLevel: Number
  ) {
    this.id = id;
    this.name = name;
    this.createdAt = createdAt;
    this.parentFishId = parentFishId;
    this.imagePath = imagePath;
    this.mood = mood;
    this.age = age;
    this.alive = alive;
    this.weight = weight;
    this.minWeight = minWeight;
    this.maxWeight = maxWeight;
    this.currentHungerLevel = currentHungerLevel;
    this.gainWeightHungerLevel = gainWeightHungerLevel;
    this.loseWeightHungerLevel = loseWeightHungerLevel;
  }
}
