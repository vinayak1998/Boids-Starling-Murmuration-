
class Boid
{
  PVector pos, acc, prey, ali, coh, sep; //pos, velocity, and acceleration in a vector datatype
  PVector vel;
  float neighborhoodRadius; //radius in which it looks for fellow boids
  float maxSpeed = 2; //maximum magnitude for the velocity vector
  float maxSteerForce = 0.05; //maximum magnitude of the steering vector
  float h; //hue
  float sc=3; //scale factor for the render of the boid
  float flap = 0;
  float t=0;
  boolean avoidWalls = false;
  float seperation_value = 40;


  //constructors
  
  void reverse(){
    vel.mult(-1);
    acc.mult(-1);
  }
  Boid(PVector inPos)
  {
    pos = new PVector(random(1500), random(1000), random(1000));
    pos.set(inPos);
    vel = new PVector(random(-1, 1), random(-1, 1), random(-1, 1));
    acc = new PVector(0, 0, 0);
    neighborhoodRadius = 100;
  }
  
  void computeboid(ArrayList bl,ArrayList al, float slider1, float slider2, float slider3, boolean type, boolean preymode)
  {
    t+=0.1;

    //acc.add(new PVector(0,.002,0));
    if (avoidWalls)
    {
      acc.add(PVector.mult(avoid(new PVector(pos.x, height, pos.z), true), 5));
      acc.add(PVector.mult(avoid(new PVector(pos.x, 0, pos.z), true), 5));
      acc.add(PVector.mult(avoid(new PVector(width, pos.y, pos.z), true), 5));
      acc.add(PVector.mult(avoid(new PVector(0, pos.y, pos.z), true), 5));
      acc.add(PVector.mult(avoid(new PVector(pos.x, pos.y, 300), true), 5));
      acc.add(PVector.mult(avoid(new PVector(pos.x, pos.y, 900), true), 5));
   }
    if (noisepresent){
       acc.add(new PVector(.01,0,0));
    }
    if (mousesteer){
      acc.add(steer(new PVector(mouseX,mouseY,pos.z+2),true));
    }
    flock(bl,slider1,slider2,slider3);
    if(preymode){
      prey = prey(al);
      acc.add(PVector.mult(prey, seperation_value*(1)));
    }
        move();
    checkBounds();
    flap = 10*sin(t);
  }
  
  
  void run(ArrayList bl,ArrayList al, float slider1, float slider2, float slider3, boolean type)
  {
    render(type);
  }

  /////-----------behaviors---------------
  void flock(ArrayList bl,float slider1, float slider2, float slider3)
  {
    ali = alignment(bl);
    coh = cohesion(bl);
    sep = seperation(bl);
    acc.add(PVector.mult(ali, slider2));
    acc.add(PVector.mult(coh, slider3));
    acc.add(PVector.mult(sep, slider1));
  }



  void move()
  {
    vel.add(acc); //add acceleration to velocity
    vel.limit(maxSpeed); //make sure the velocity vector magnitude does not exceed maxSpeed
    pos.add(vel); //add velocity to position
    acc.mult(0); //reset acceleration
  }

  void checkBounds()
  {
    if (pos.x>width) pos.x=0;
    if (pos.x<0) pos.x=width;
    if (pos.y>height) pos.y=0;
    if (pos.y<0) pos.y=height;
    if (pos.z>900) pos.z=300;
    if (pos.z<300) pos.z=900;
  }

  void render(boolean type)
  {

    pushMatrix();
    translate(pos.x, pos.y, pos.z);
    rotateY(atan2(-vel.z, vel.x));
    rotateZ(asin(vel.y/vel.mag()));
    noStroke();
    fill(h);
    beginShape(TRIANGLES);
    vertex(3*sc,0,0);
    vertex(-3*sc,2*sc,0);
    vertex(-3*sc,-2*sc,0);

    vertex(3*sc,0,0);
    vertex(-3*sc,2*sc,0);
    vertex(-3*sc,0,2*sc);

    vertex(3*sc,0,0);
    vertex(-3*sc,0,2*sc);
    vertex(-3*sc,-2*sc,0);


    if(type==true) //=>bird
    {    
    vertex(2*sc, 0, 0);
    vertex(-1*sc, 0, 0);
    vertex(-1*sc, -8*sc, flap);

    vertex(2*sc, 0, 0);
    vertex(-1*sc, 0, 0);
    vertex(-1*sc, 8*sc, flap);


    vertex(-3*sc, 0, 2*sc);
    vertex(-3*sc, 2*sc, 0);
    vertex(-3*sc, -2*sc, 0);
    }
    else //=>fish
    {
    }
      
    endShape();
    popMatrix();
  }

  PVector steer(PVector target, boolean arrival)
  {
    PVector steer = new PVector(); //creates vector for steering
    if (!arrival)
    {
      steer.set(PVector.sub(target, pos)); //steering vector points towards target (switch target and pos for avoiding)
      steer.limit(maxSteerForce); //limits the steering force to maxSteerForce
    } else
    {
      PVector targetOffset = PVector.sub(target, pos);
      float distance=targetOffset.mag();
      float rampedSpeed = maxSpeed*(distance/100);
      float clippedSpeed = min(rampedSpeed, maxSpeed);
      PVector desiredVelocity = PVector.mult(targetOffset, (clippedSpeed/distance));
      steer.set(PVector.sub(desiredVelocity, vel));
    }
    return steer;
  }



  //avoid. If weight == true avoidance vector is larger the closer the boid is to the target
  PVector avoid(PVector target, boolean weight)
  {
    PVector steer = new PVector(); //creates vector for steering
    steer.set(PVector.sub(pos, target)); //steering vector points away from target
    if (weight)
      steer.mult(1/sq(PVector.dist(pos, target)));
    //steer.limit(maxSteerForce); //limits the steering force to maxSteerForce
    return steer;
  }

  PVector seperation(ArrayList boids)
  {
    PVector posSum = new PVector(0, 0, 0);
    PVector repulse;
    for (int i=0; i<boids.size(); i++)
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

  PVector alignment(ArrayList boids)
  {
    PVector velSum = new PVector(0, 0, 0);
    int count = 0;
    for (int i=0; i<boids.size(); i++)
    {
      Boid b = (Boid)boids.get(i);
      float d = PVector.dist(pos, b.pos);
      if (d>0&&d<=neighborhoodRadius)
      {
        velSum.add(b.vel);
        count++;
      }
    }
    if (count>0)
    {
      velSum.div((float)count);
      velSum.limit(maxSteerForce);
    }
    return velSum;
  }

  PVector cohesion(ArrayList boids)
  {
    PVector posSum = new PVector(0, 0, 0);
    PVector steer = new PVector(0, 0, 0);
    int count = 0;
    for (int i=0; i<boids.size(); i++)
    {
      Boid b = (Boid)boids.get(i);
      float d = dist(pos.x, pos.y, b.pos.x, b.pos.y);
      if (d>0&&d<=neighborhoodRadius)
      {
        posSum.add(b.pos);
        count++;
      }
    }
    if (count>0)
    {
      posSum.div((float)count);
    }
    steer = PVector.sub(posSum, pos);
    steer.limit(maxSteerForce); 
    return steer;
  }
  
 PVector prey(ArrayList avoids)
  {
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
    return posSum;  
}
  
 
//////EOF 
}
