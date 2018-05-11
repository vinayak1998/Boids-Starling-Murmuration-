import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import controlP5.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class simulation extends PApplet {



ControlP5 cp5;

PFont pfont;
int fontSize;
float seperate = 1;
float align = 1;
float cohere = 1;
float h = 255;
float zoom=-800;

float varY=0;
float varX=0;

int numboids = 1000;
int numpreys =1;
ArrayList<Avoid> avoids;
ArrayList<Boid> boids;

boolean tool = true;
boolean avoidWalls = true;
boolean type = false;
boolean noisepresent = false;
boolean mousesteer = false;
boolean threadingon = true;
boolean preymode = true;
boolean background = false;

public void setup(){
  boids = new ArrayList<Boid>();
  avoids = new ArrayList<Avoid>();
  for(int i=0;i<numboids;i++){
   boids.add(new Boid(new PVector(width/2,height/2,600)));
  }
  for(int i=0;i<numpreys;i++){
   avoids.add(new Avoid(new PVector(width/2,height/2,600)));
  }
  //sliders
  cp5 = new ControlP5(this);
  cp5.setAutoDraw(false);
  pfont = createFont("Advent", 40, true); // use true/false for smooth/no-smooth
  fontSize=30;  
  setupc5();
  if(threadingon){
  threading(numboids,500);
  }
}

public void reverse(){
    for(int i=0;i<boids.size();i++){ 
      Boid tempBoid = (Boid)boids.get(i); 
      tempBoid.reverse(); 
     }
  }


public void run(boolean aW, float slider1,float slider2,float slider3, boolean type){
    for(int i=0;i<boids.size();i++){
      Boid tempBoid = boids.get(i); 
      tempBoid.h = h;
      tempBoid.avoidWalls = aW;
      tempBoid.run(boids, avoids ,slider1,slider2,slider3, type); 
    }
    for(int i=0;i<avoids.size();i++){
      Avoid tempBoid = avoids.get(i);
      tempBoid.h = h;
      tempBoid.avoidWalls = aW;
      tempBoid.run(avoids, boids, preymode); 
    }
  }

public void computepartly(boolean aW, float slider1,float slider2,float slider3,int a,int b,boolean type)
  {
    for(int i=a;i<b;i++) //iterate through the list of boids
    {
      Boid tempBoid = (Boid)boids.get(i); //create a temporary boid to process and make it the current boid in the list
      tempBoid.h = h;
      tempBoid.avoidWalls = aW;
      tempBoid.computeboid(boids,avoids, slider1,slider2,slider3, type, preymode); //tell the temporary boid to execute its run method
    }
  }
  
  public void draw(){
  pushMatrix();
  textFont(pfont,fontSize);
  fill(0);
  
 
  beginCamera();
  camera();
  rotateX(varY);
  rotateY(varX);
  translate(0,0,zoom);
  endCamera();
  if(background){
  PImage img;
  img = loadImage("sky.jpg");
  background(img);
  }
  else{
  background(0xff217CA3);
  }
  directionalLight(255,255,255, 0, 1, -100);
  if(!threadingon){
  computepartly(avoidWalls, seperate, align, cohere,0,boids.size(), type);
   }
   run(avoidWalls, seperate, align, cohere, type);
  popMatrix();
  text("Total Boid: " + str(boids.size()), 10, 40);
  text("Energy" + str(energy()), 350,40);
  text("Momentum" + str(momentum().mag()), 600,40);
}

public int energy(){
  int energy =0;
  for (Boid b: boids){
    energy += b.vel.dot(b.vel);
  }
  return energy;
}

public PVector momentum(){
  PVector momentum;
  momentum = new PVector(0,0);
  for(Boid b: boids){
// momentum.add((b.pos.sub(new PVector(width/2, height/2))).cross(b.vel));        
// correct line but not working
    momentum.add(b.pos.cross(b.vel)).div(100);
  }
  return momentum;
}

public void mouseDragged() {
  if (mouseX > 0 && mouseX < width && 
      mouseY > 0 && mouseY < height -160){
        varY += (map(mouseY-pmouseY,0,height,0,TWO_PI));
        varX += (map(mouseX-pmouseX,width,0,0,TWO_PI));
      }
}

public void mousePressed () {
  if (mouseX > 0 && mouseX < width && 
      mouseY > 0 && mouseY < height -160){ 
  if (tool) {
    boids.add(new Boid(new PVector(mouseX,mouseY,600)));
   }
  else{
    avoids.add(new Avoid(new PVector(mouseX, mouseY, 600)));
  }
      }
}

public void keyPressed(){
  switch (keyCode){
    case UP: zoom-=100; break;
    case DOWN: zoom+=100; break;
  }
  switch (key){
    case 'a': avoidWalls = !avoidWalls; break;
    case 'r': reverse(); break;
    case 't' : type = !type; break;
    case 'b' : tool = !tool;break;
    case 'n' : noisepresent = !noisepresent;break;
    case 's' : mousesteer = !mousesteer;break;
    case 'm' : threadingon = true;break;
    case 'p' : preymode = !preymode;
}  
}

Knob sep;
Knob ali;
Knob coh;







///////////////////////////////
public void settings() {
  size(1440, 768 ,P3D);  
}
public void setupc5(){
  ControlFont font = new ControlFont(pfont, 241);
   int size = 200;

  
  cp5 = new ControlP5(this);

//Info

//Simulation for flocking bird behaviour.
//Its running right now on multithreading mode

//Keyboard controls:-
   //'a': trigger avoidWalls
   // 'r': trigger direction of bodies
   //'t' : type = run simulation in boid/bird mode;
   // 'b' : tool = trigger add to boids/preys.
   // 'n' : noisepresent = add windcontribution 
   // 's' : mousesteer = turn on/off mouse following
   // 'p' : toggle prey mode.

    Group g1 = cp5.addGroup("Info")
                .setPosition(width-size-20,20)
                .setBackgroundHeight(320)
                .setWidth(size+10)
                .setBackgroundColor(color(255,50))
                ;
                     
cp5.addTextlabel("sim")
                    .setText("Simulation for flocking bird behaviour")
                    .setPosition(10,20)
                    //.setColorValue(0xffffff00)
                    //.setFont(createFont("Georgia",15))
                    .setGroup(g1)
                    ;
          
if(threadingon){
cp5.addTextlabel("mode")
                    .setText("Its running right now on multithreading mode")
                    .setPosition(10,60)
                    //.setColorValue(0xffffff00)
                    //.setFont(createFont("Georgia",15))
                    .setGroup(g1)
                    ;
}
else{
cp5.addTextlabel("mode")
                    .setText("Its running right now not using multithreading ")
                    .setPosition(10,60)
                    //.setColorValue(0xffffff00)
                    //.setFont(createFont("Georgia",15))
                    .setGroup(g1)
                    ;
}

cp5.addTextlabel("key")
                    .setText("Keyboard controls:- \n'a': trigger avoidWalls \n'r': trigger direction of bodies \n't' : type = run simulation in boid/bird mode \n'b' : tool = trigger add to boids/preys \n'n' : noisepresent = add windcontribution \n's' : mousesteer = turn on/off mouse following \n'p' : toggle prey mode")
                    .setPosition(10,100)
                    //.setColorValue(0xffffff00)
                    //.setFont(createFont("Georgia",15))
                    .setGroup(g1)
                    .setLineHeight(20) 

                    ;    

                                        
  sep = cp5.addKnob("sep")
               .setPosition(100,height-160)
               .setRange(0,10)
               .setColorCaptionLabel(10000)
               .setRadius(60)
               .setDragDirection(Knob.VERTICAL)
               ;
                     
  sep.setCaptionLabel("Seperate")
    .getCaptionLabel()
      .setFont(font)
        .toUpperCase(false)
          .setSize(fontSize-10)
            ;
  ali = cp5.addKnob("ali")
               .setPosition(250,height-160)
               .setRange(0,10)
               .setColorCaptionLabel(10000)
               .setRadius(60)
               .setDragDirection(Knob.VERTICAL)
               ;
                     
  ali.setCaptionLabel("Align")
    .getCaptionLabel()
      .setFont(font)
        .toUpperCase(false)
          .setSize(fontSize-10)
            ;
  coh = cp5.addKnob("coh")
               .setPosition(400,height-160)
               .setRange(0,10)
               .setColorCaptionLabel(10000)
               .setRadius(60)
               .setDragDirection(Knob.VERTICAL)
               ;
                     
  coh.setCaptionLabel("Cohesion")
    .getCaptionLabel()
      .setFont(font)
        .toUpperCase(false)
          .setSize(fontSize-10)
            ;
    //case 'r': reverse(); break;
  cp5.addToggle("type")
     .setPosition(width-200,height-150)
     .setSize(50,20)
     ;
  cp5.addToggle("preymode")
     .setPosition(width-200,height-100)
     .setSize(50,20)
     ;
  cp5.addToggle("tool")
     .setPosition(width-200,height-50)
     .setSize(50,20)
     ;
  cp5.addToggle("mousesteer")
     .setPosition(width-100,height-150)
     .setSize(50,20)
     ;

  cp5.addToggle("noisepresent")
     .setPosition(width-100,height-100)
     .setSize(50,20)
     ;
  cp5.addToggle("avoidwalls")
     .setPosition(width-100,height-50)
     .setSize(50,20)
     ;
  cp5.addToggle("background")
     .setPosition(width-300,height-125)
     .setSize(50,20)
     ;


}
  
  class copThread extends Thread {
  boolean running;
  int option;
  int r1,r2;
  copThread(){};
  copThread(int a){
  this.running = false;
  this.option = a;
  }
  copThread(int a1,int a2){
   this.r1 =a1;
   this.r2 = a2;
  }
    public void start()
  {
    running = true;
    super.start();
  }
  
  public void run(){
    while(running){
      computepartly(avoidWalls, seperate, align, cohere,r1,r2, type);
    }
  }
}

public void threading(int n,int interval){
  int q,r;
  q = n/interval;
  r = n%interval;
  copThread[] threadq = new copThread[n];
  for(int i=0;i<q;i++){
    threadq[i] = new copThread(i*interval,(i+1)*interval);
    threadq[i].start();
  }
  if(r!=0){
    threadq[q] = new copThread(q*interval,n);
    threadq[q].start();
  }
}
class Avoid {
   PVector pos, acc, prey, sep, p; 
  PVector vel;
  float neighborhoodRadius; 
  float maxSpeed = 2; 
  float sc=3; //scale factor for the render of the Avoid
  float maxSteerForce = 0.05f; 
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
  
  public void run(ArrayList al, ArrayList bl, boolean preymode){
    t+=0.1f;

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
       acc.add(new PVector(0.01f,0,0));
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



  public void move(){
    vel.add(acc); //add acceleration to velocity
    vel.limit(maxSpeed); //make sure the velocity vector magnitude does not exceed maxSpeed
    pos.add(vel); //add velocity to position
    acc.mult(0); //reset acceleration
  }

  public void checkBounds(){
    if (pos.x>width) pos.x=0;
    if (pos.x<0) pos.x=width;
    if (pos.y>height) pos.y=0;
    if (pos.y<0) pos.y=height;
    if (pos.z>900) pos.z=300;
    if (pos.z<300) pos.z=900;
  }

  public void render(){
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

  public PVector steer(PVector target)
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
  public PVector avoid(PVector target, boolean weight)
  {
    PVector steer = new PVector(); //creates vector for steering
    steer.set(PVector.sub(pos, target)); //steering vector points away from target
    if (weight)
      steer.mult(1/sq(PVector.dist(pos, target)));
    //steer.limit(maxSteerForce); //limits the steering force to maxSteerForce
    //print("avoid : "+str(steer.mag()));    
    return steer;
  }

  public PVector seperation(ArrayList avoids){
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
  
  public PVector prey(ArrayList boids)
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

class Boid
{
  PVector pos, acc, prey, ali, coh, sep; //pos, velocity, and acceleration in a vector datatype
  PVector vel;
  float neighborhoodRadius; //radius in which it looks for fellow boids
  float maxSpeed = 2; //maximum magnitude for the velocity vector
  float maxSteerForce = 0.05f; //maximum magnitude of the steering vector
  float h; //hue
  float sc=3; //scale factor for the render of the boid
  float flap = 0;
  float t=0;
  boolean avoidWalls = false;
  float seperation_value = 40;


  //constructors
  
  public void reverse(){
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
  
  public void computeboid(ArrayList bl,ArrayList al, float slider1, float slider2, float slider3, boolean type, boolean preymode)
  {
    t+=0.1f;

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
       acc.add(new PVector(.01f,0,0));
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
  
  
  public void run(ArrayList bl,ArrayList al, float slider1, float slider2, float slider3, boolean type)
  {
    render(type);
  }

  /////-----------behaviors---------------
  public void flock(ArrayList bl,float slider1, float slider2, float slider3)
  {
    ali = alignment(bl);
    coh = cohesion(bl);
    sep = seperation(bl);
    acc.add(PVector.mult(ali, slider2));
    acc.add(PVector.mult(coh, slider3));
    acc.add(PVector.mult(sep, slider1));
  }



  public void move()
  {
    vel.add(acc); //add acceleration to velocity
    vel.limit(maxSpeed); //make sure the velocity vector magnitude does not exceed maxSpeed
    pos.add(vel); //add velocity to position
    acc.mult(0); //reset acceleration
  }

  public void checkBounds()
  {
    if (pos.x>width) pos.x=0;
    if (pos.x<0) pos.x=width;
    if (pos.y>height) pos.y=0;
    if (pos.y<0) pos.y=height;
    if (pos.z>900) pos.z=300;
    if (pos.z<300) pos.z=900;
  }

  public void render(boolean type)
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

  public PVector steer(PVector target, boolean arrival)
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
  public PVector avoid(PVector target, boolean weight)
  {
    PVector steer = new PVector(); //creates vector for steering
    steer.set(PVector.sub(pos, target)); //steering vector points away from target
    if (weight)
      steer.mult(1/sq(PVector.dist(pos, target)));
    //steer.limit(maxSteerForce); //limits the steering force to maxSteerForce
    return steer;
  }

  public PVector seperation(ArrayList boids)
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

  public PVector alignment(ArrayList boids)
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

  public PVector cohesion(ArrayList boids)
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
  
 public PVector prey(ArrayList avoids)
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
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "simulation" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
