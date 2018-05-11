import controlP5.*;

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

void setup(){
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

void reverse(){
    for(int i=0;i<boids.size();i++){ 
      Boid tempBoid = (Boid)boids.get(i); 
      tempBoid.reverse(); 
     }
  }


void run(boolean aW, float slider1,float slider2,float slider3, boolean type){
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

void computepartly(boolean aW, float slider1,float slider2,float slider3,int a,int b,boolean type)
  {
    for(int i=a;i<b;i++) //iterate through the list of boids
    {
      Boid tempBoid = (Boid)boids.get(i); //create a temporary boid to process and make it the current boid in the list
      tempBoid.h = h;
      tempBoid.avoidWalls = aW;
      tempBoid.computeboid(boids,avoids, slider1,slider2,slider3, type, preymode); //tell the temporary boid to execute its run method
    }
  }
  
  void draw(){
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
  background(#217CA3);
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

int energy(){
  int energy =0;
  for (Boid b: boids){
    energy += b.vel.dot(b.vel);
  }
  return energy;
}

PVector momentum(){
  PVector momentum;
  momentum = new PVector(0,0);
  for(Boid b: boids){
// momentum.add((b.pos.sub(new PVector(width/2, height/2))).cross(b.vel));        
// correct line but not working
    momentum.add(b.pos.cross(b.vel)).div(100);
  }
  return momentum;
}

void mouseDragged() {
  if (mouseX > 0 && mouseX < width && 
      mouseY > 0 && mouseY < height -160){
        varY += (map(mouseY-pmouseY,0,height,0,TWO_PI));
        varX += (map(mouseX-pmouseX,width,0,0,TWO_PI));
      }
}

void mousePressed () {
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

void keyPressed(){
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
void settings() {
  size(1440, 768 ,P3D);  
}
void setupc5(){
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
    void start()
  {
    running = true;
    super.start();
  }
  
  void run(){
    while(running){
      computepartly(avoidWalls, seperate, align, cohere,r1,r2, type);
    }
  }
}

void threading(int n,int interval){
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
