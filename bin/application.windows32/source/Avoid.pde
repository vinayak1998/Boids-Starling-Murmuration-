class Avoid {
   PVector pos, acc, prey, sep, p; 
  PVector vel;
  float neighborhoodRadius; 
  float maxSpeed = 2; 
  float sc=3; //scale factor for the render of the Avoid
  float maxSteerForce = 0.05; 
  float flap = 0;
  float h; //hue
  float t=0;
  float seperation_value = 4;
  boolean avoidWalls = false;
  


  
   Avoid(PVector inPos){
    pos = new PVector(random(1500), random(1000), random(1000));
    pos.set(inPos);
    vel = new PVector(random(-1, 1), random(-1, 1), random(-1, 1));
    acc = new PVector(0, 0, 0);
    neighborhoodRadius = 100;
    p = new PVector(random(1500), random(1000), random(1000));
    neighborhoodRadius = 100;
  }
  
  void run(ArrayList al, ArrayList bl, boolean preymode){
    t+=0.1;

//    acc.add(steer(new PVector(mouseX,mouseY,300),true));
   //for(int i=0;i<bl.size();i++){ //iterate through the list of boids{
   //   Boid tempBoid = (Boid)bl.get(i); //create a temporary boid to process and make it the current boid in the list
   //   p = tempBoid.pos;
   //   float d = pos.sub(p).mag();
   //   if(d<5 && d>0){
   //     acc.add(steer(p));
   //   }
   //}
    //acc.add(new PVector(0,.002,0));
    if(preymode){
    if (avoidWalls){
      acc.add(PVector.mult(avoid(new PVector(pos.x, height, pos.z), true), 5));
      acc.add(PVector.mult(avoid(new PVector(pos.x, 0, pos.z), true), 5));
      acc.add(PVector.mult(avoid(new PVector(width, pos.y, pos.z), true), 5));
      acc.add(PVector.mult(avoid(new PVector(0, pos.y, pos.z), true), 5));
      acc.add(PVector.mult(avoid(new PVector(pos.x, pos.y, 300), true), 5));
      acc.add(PVector.mult(avoid(new PVector(pos.x, pos.y, 900), true), 5));
      //acc.add(PVector.mult(pos.sub(1500,1000,1000), 2));
    }
    if (noisepresent){
       acc.add(new PVector(0.01,0,0));
    }
    sep = seperation(al);
    acc.add(PVector.mult(sep, 2));
    prey = prey(bl);
    acc.add(PVector.mult(prey, 4));
    
    move();
    checkBounds();
    flap = 10*sin(t);
    render();
    }
  }



  void move(){
    vel.add(acc); //add acceleration to velocity
    vel.limit(maxSpeed); //make sure the velocity vector magnitude does not exceed maxSpeed
    pos.add(vel); //add velocity to position
    acc.mult(0); //reset acceleration
  }

  void checkBounds(){
    if (pos.x>width) pos.x=0;
    if (pos.x<0) pos.x=width;
    if (pos.y>height) pos.y=0;
    if (pos.y<0) pos.y=height;
    if (pos.z>900) pos.z=300;
    if (pos.z<300) pos.z=900;
  }

  void render(){
    pushMatrix();
    translate(pos.x, pos.y, pos.z);
    rotateY(atan2(-vel.z, vel.x));
    rotateZ(asin(vel.y/vel.mag()));
    noStroke();
    fill(h);
    box(10);  
    endShape();
    popMatrix();
  }

  PVector steer(PVector target)
  {
    PVector steer = new PVector(); //creates vector for steering
    {
      PVector targetOffset = PVector.sub(target, pos);
      float distance=targetOffset.mag();
      float rampedSpeed = maxSpeed*(distance/100);
      float clippedSpeed = min(rampedSpeed, maxSpeed);
      PVector desiredVelocity = PVector.mult(targetOffset, (clippedSpeed/distance));
      steer.set(PVector.sub(desiredVelocity, vel));
    }
    //print("steer : "+str(steer.mag()));
    return steer;
  }



  //avoid. If weight == true avoidance vector is larger the closer the Avoid is to the target
  PVector avoid(PVector target, boolean weight)
  {
    PVector steer = new PVector(); //creates vector for steering
    steer.set(PVector.sub(pos, target)); //steering vector points away from target
    if (weight)
      steer.mult(1/sq(PVector.dist(pos, target)));
    //steer.limit(maxSteerForce); //limits the steering force to maxSteerForce
    //print("avoid : "+str(steer.mag()));    
    return steer;
  }

  PVector seperation(ArrayList avoids){
    PVector posSum = new PVector(0, 0, 0);
    PVector repulse;
    for (int i=0; i<avoids.size(); i++)
    {
      Avoid b = (Avoid)avoids.get(i);
      float d = PVector.dist(pos, b.pos);
      if (d>0&&d<=neighborhoodRadius)
      {
        repulse = PVector.sub(pos, b.pos);
        repulse.normalize();
        repulse.div(d);
        posSum.add(repulse);
      }
    }
   // print("seperation : "+str(posSum.mag()));

    return posSum;
  }
  
  PVector prey(ArrayList boids)
  {
    PVector posSum = new PVector(0, 0, 0);
    PVector repulse;
    for (int i=0; i<avoids.size(); i++)
    {
      Boid b = (Boid)boids.get(i);
      float d = PVector.dist(pos, b.pos);
      if (d>0&&d<=neighborhoodRadius)
      {
        repulse = PVector.sub(pos, b.pos);
        repulse.normalize();
        repulse.div(d);
        posSum.add(repulse);
      }
    }
    return posSum;  

  }
  
  
  
   
}
